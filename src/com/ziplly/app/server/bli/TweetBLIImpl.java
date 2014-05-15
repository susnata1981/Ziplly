package com.ziplly.app.server.bli;

import java.util.Date;
import java.util.List;

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
import com.ziplly.app.model.Account;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.Subscription;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanType;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;

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

	@Override
	public Tweet sendTweet(final Tweet tweet, final Account loggedInAccount) throws AccessException,
	    NeedsSubscriptionException,
	    InternalException,
	    UsageLimitExceededException,
	    NotFoundException {

		// check usage limits for business tweets
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
		List<SubscriptionPlan> subscriptionPlans = subscriptionPlanDao.getAll();
		SubscriptionPlan activePlan = getActiveSubscriptionPlan(subscriptionPlans, subscription);

		if (isCouponPromotion(tweet)) {
			if (!isCouponPromotionWithinLimit(subscription, activePlan, tweet)) {
				throw new NeedsSubscriptionException(StringConstants.NEEDS_SUBSCRIPTION_EXCEPTION);
			}
			// otherwise it's fine
			return;
		}

		if (!isTweetPromotionWithinLimit(subscription, activePlan, tweet)) {
			throw new NeedsSubscriptionException(StringConstants.NEEDS_SUBSCRIPTION_EXCEPTION);
		}
	}

	private SubscriptionPlan
	    getActiveSubscriptionPlan(final List<SubscriptionPlan> subscriptionPlans,
	        final Subscription activeSubscription) {

		if (activeSubscription == null) {
			return getSubscriptionPlan(subscriptionPlans, SubscriptionPlanType.BASIC);
		} else {
			return activeSubscription.getSubscriptionPlan();
		}
	}

	// Unit test this function.
	Date getLastSubscriptionCycle(Date subscriptionCreatedOn) {
		DateTime now = new DateTime();
		DateTime subscriptionCreationTime = new DateTime(subscriptionCreatedOn);
		int subCycleDayOfMonth = subscriptionCreationTime.getDayOfMonth();
		int currDayOfMonth = now.getDayOfMonth();

		if (subCycleDayOfMonth > currDayOfMonth) {
			return now.minusMonths(1).plusDays(subCycleDayOfMonth - currDayOfMonth).toDate();
		} else if (subCycleDayOfMonth < currDayOfMonth) {
			DateTime lastBillingTime =
			    now.minusMonths(1).minusDays(currDayOfMonth).plusDays(subCycleDayOfMonth);
			return lastBillingTime.toDate();
		} else {
			return now.toDate();
		}
	}

	private boolean isTweetPromotionWithinLimit(Subscription subscription, 
			SubscriptionPlan plan, 
			Tweet tweet) {
		
		long totalTweetCount = 0;
		Date now = new Date();
		
		if (subscription == null) {
			totalTweetCount = tweetDao.findTweetsByAccountIdAndMonth(tweet.getSender().getAccountId(), new Date());
		} else {
			Date subscriptionCreatedOn = subscription.getTimeCreated();
			Date lastSubscriptionCycle = getLastSubscriptionCycle(subscriptionCreatedOn);
			totalTweetCount = tweetDao.findTotalTweetsPublishedBetween(tweet.getSender().getAccountId(), lastSubscriptionCycle, now);
		}
		
		return plan.getTweetsAllowed() >= totalTweetCount;
	}

	private boolean isCouponPromotionWithinLimit(Subscription subscription,
	    SubscriptionPlan plan,
	    Tweet tweet) {

		Date now = new Date();
		Long totalCouponCount = 0L;
		
		if (subscription == null) {
			totalCouponCount =
			    tweetDao.findTotalCouponsByAccountIdAndMonth(tweet.getSender().getAccountId(), now);
		} else {
			Date subscriptionCreatedOn = subscription.getTimeCreated();
			Date lastSubscriptionCycle = getLastSubscriptionCycle(subscriptionCreatedOn);
			totalCouponCount = tweetDao.findTotalCouponsPublishedBetween(
					tweet.getSender().getAccountId(),
			    lastSubscriptionCycle,
			    now);
		}
		
		return plan.getCouponsAllowed() >= totalCouponCount;
	}

	private SubscriptionPlan getSubscriptionPlan(List<SubscriptionPlan> subscriptionPlans,
	    SubscriptionPlanType type) {
		for (SubscriptionPlan plan : subscriptionPlans) {
			if (plan.getPlanType() == type) {
				return plan;
			}
		}
		return null;
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
