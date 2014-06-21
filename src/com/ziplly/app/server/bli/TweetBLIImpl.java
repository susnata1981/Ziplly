package com.ziplly.app.server.bli;

import java.util.Date;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.BusinessAccount;
import com.ziplly.app.server.model.jpa.Subscription;
import com.ziplly.app.server.model.jpa.SubscriptionPlan;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.server.util.TimeUtil;

public class TweetBLIImpl implements TweetBLI {
  private TweetDAO tweetDao;
  private TweetNotificationBLI tweetNotificationBli;
  private SubscriptionPlanDAO subscriptionPlanDao;

  @Inject
  public TweetBLIImpl(TweetDAO tweetDao,
      TweetNotificationBLI tweetNotificationBLI,
      SubscriptionPlanDAO subscriptionPlanDao) {
    this.tweetDao = tweetDao;
    this.tweetNotificationBli = tweetNotificationBLI;
    this.subscriptionPlanDao = subscriptionPlanDao;
  }

  /**
   * Main method for sending tweets.
   */
  @Override
  public Tweet sendTweet(final Tweet tweet, final Account loggedInAccount) throws AccessException,
      NeedsSubscriptionException,
      InternalException,
      UsageLimitExceededException,
      NotFoundException {

    // Check usage limits for business tweets
    if (loggedInAccount instanceof BusinessAccount) {
      checkUsage((BusinessAccount) loggedInAccount, tweet);
    }

    // Only business accounts can publish this
    if (isCouponPromotion(tweet)) {
      checkAccountType(loggedInAccount);
    }

    TweetDTO savedTweet = tweetDao.save(tweet);
    tweetNotificationBli.sendNotificationsIfRequired(savedTweet);

    return tweet;
  }

  private boolean isCouponPromotion(Tweet tweet) {
    return tweet.getCoupon() != null;
  }

  /**
   * Checks to see if the current business account has permission to publish
   * coupons
   * 
   * @throws AccessException
   */
  private void checkAccountType(Account account) throws AccessException {
    if (!(account instanceof BusinessAccount)) {
      throw new AccessException();
    }
  }

  private void
      checkUsage(final BusinessAccount account, final Tweet tweet) throws NeedsSubscriptionException,
          InternalException,
          UsageLimitExceededException,
          NotFoundException {

    boolean enablePaymentPlan = FeatureFlags.EnablePaymentPlan.isEnabled();

    // Wire on Wire off
    if (!enablePaymentPlan) {
      return;
    }

    Subscription subscription = getActiveSubscription(account);

    if (subscription == null) {
      throw new NeedsSubscriptionException(StringConstants.NEEDS_SUBSCRIPTION_EXCEPTION);
    }

    SubscriptionPlan activePlan = subscription.getSubscriptionPlan();

    // Must be subscribed to publish coupons
    if (activePlan == null) {
      throw new NeedsSubscriptionException(StringConstants.NEEDS_SUBSCRIPTION_EXCEPTION);
    }

    if (isCouponPromotion(tweet)) {
      if (!isCouponPromotionWithinLimit(subscription, activePlan, tweet)) {
        throw new UsageLimitExceededException(String.format("Can't publish more than %d coupons with %s plan", 
            activePlan.getCouponsAllowed(), activePlan.getName()));

      }
      // otherwise it's fine
      return;
    }

    if (!isTweetPromotionWithinLimit(subscription, activePlan, tweet)) {
      throw new UsageLimitExceededException(String.format("Can't publish more than %d tweets with %s plan", 
          activePlan.getTweetsAllowed(), activePlan.getName()));
    }
  }

  private boolean isTweetPromotionWithinLimit(Subscription subscription,
      SubscriptionPlan plan,
      Tweet tweet) {

    if (subscription == null) {
      return false;
    }

    Date now = new Date();
    System.out.println("T = " + now.getTime() + " N = " + now);
    DateTime lastSubscriptionCycle = getLastSubscriptionCycle(now, subscription.getTimeCreated());
    System.out.println("T = " + lastSubscriptionCycle + " B = " + lastSubscriptionCycle);
    
    long totalTweetCount =
        tweetDao.findTotalTweetsPublishedBetween(
            tweet.getSender().getAccountId(),
            lastSubscriptionCycle.toDate(),
            now);
    return plan.getTweetsAllowed() > totalTweetCount;
  }

  // Unit test this function.
  DateTime getLastSubscriptionCycle(Date now, Date subscriptionCreatedOn) {
    DateTime subscriptionCreationTime = TimeUtil.toDateTime(subscriptionCreatedOn.getTime());
    int subCycleDayOfMonth = subscriptionCreationTime.getDayOfMonth();
    DateTime dateTime = new DateTime(now);
    int currDayOfMonth = dateTime.getDayOfMonth();
    DateTime lastBillingTime = null;

    if (subCycleDayOfMonth > currDayOfMonth) {
      lastBillingTime = dateTime.minusMonths(1).plusDays(subCycleDayOfMonth - currDayOfMonth);
//      return Math.max(lastBillingTime.getMillis(), subscriptionCreationTime.getMillis());
    } else if (subCycleDayOfMonth < currDayOfMonth) {
      lastBillingTime = dateTime.minusMonths(1).minusDays(currDayOfMonth).plusDays(subCycleDayOfMonth);
    } else {
      lastBillingTime = dateTime.minusMonths(1);
    }
    
    return max(lastBillingTime, subscriptionCreationTime);
  }

  private DateTime max(DateTime a, DateTime b) {
    return a.isAfter(b) ? a : b;
  }
  
  private boolean isCouponPromotionWithinLimit(Subscription subscription,
      SubscriptionPlan plan,
      Tweet tweet) {

    Date now = new Date();
    long totalCouponCount = 0L;
    DateTime lastSubscriptionCycle = getLastSubscriptionCycle(now, subscription.getTimeCreated());
    totalCouponCount =
        tweetDao.findTotalCouponsPublishedBetween(
            tweet.getSender().getAccountId(),
            lastSubscriptionCycle.toDate(),
            now);

    return plan.getCouponsAllowed() > totalCouponCount;
  }

  private Subscription getActiveSubscription(BusinessAccount account) {
    for (Subscription subscription : account.getSubscriptions()) {
      if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
        return subscription;
      }
    }
    return null;
  }
}
