package com.ziplly.app.server.bli;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SubscriptionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.model.SubscriptionPlanType;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.BusinessAccount;
import com.ziplly.app.server.model.jpa.Subscription;
import com.ziplly.app.server.model.jpa.SubscriptionPlan;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.server.util.TimeUtil;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public class SubscriptionBLIImpl implements SubscriptionBLI {
  private SubscriptionPlanDAO subscriptionPlanDao;
  private SubscriptionDAO subscriptionDao;
  private TransactionDAO transactionDao;
  private PaymentService paymentService;
  private Logger logger = Logger.getLogger(SubscriptionBLIImpl.class.getName());
  private AccountDAO accountDao;
  private TweetNotificationBLI tweetNotificationBli;

  @Inject
  public SubscriptionBLIImpl(AccountDAO accountDao,
      SubscriptionPlanDAO subscriptionPlanDao,
      TransactionDAO transactionDao,
      SubscriptionDAO subscriptionDao,
      PaymentService paymentService,
      TweetNotificationBLI tweetNotificationBli) {
    this.accountDao = accountDao;
    this.subscriptionPlanDao = subscriptionPlanDao;
    this.subscriptionDao = subscriptionDao;
    this.transactionDao = transactionDao;
    this.paymentService = paymentService;
    this.tweetNotificationBli = tweetNotificationBli;
  }

  @Override
  public SubscriptionEligibilityStatus checkSellerEligibility(BusinessAccount account) {
    List<Subscription> subscriptions = subscriptionDao.findByAccountId(account.getAccountId());

    if (subscriptions.size() == 0) {
      return SubscriptionEligibilityStatus.ELIGIBLE;
    }

    Subscription subscription = account.getLatestSubscription();

    if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
      return SubscriptionEligibilityStatus.ELIGIBLE;
    } else if (subscription.getStatus() == SubscriptionStatus.ACTIVE
        && subscription.getSubscriptionPlan().getPlanType() == SubscriptionPlanType.BASIC) {
      // Only basic plans allowed to upgrade from UI
      return SubscriptionEligibilityStatus.ELIGIBLE;
    } else {
      return SubscriptionEligibilityStatus.INELIGIBLE;
    }
  }

  @Override
  public String getJwtToken(Long accountId, Long subscriptionId) throws InternalException {
    SubscriptionPlan plan = subscriptionPlanDao.get(subscriptionId);
    try {
      return paymentService.generateJWTTokenForSubscription(plan, accountId);
    } catch (InvalidKeyException e) {
      logger.severe(String.format("Failed to generate token %s", e));
      throw new InternalException(String.format("Failed to initiate pay"));
    } catch (SignatureException e) {
      logger.severe(String.format("Failed to generate token %s", e));
      throw new InternalException(String.format("Failed to initiate pay"));
    }
  }

  @Transactional
  @Override
  public void
      completeTransaction(Long accountId, Long subscriptionId, String orderId) throws NotFoundException {
    
    logger.info(String.format("Completing subscription request account %d, subscription %d, order %s", 
        accountId, subscriptionId, orderId));
    
    // Load the plan
    SubscriptionPlan plan = subscriptionPlanDao.get(subscriptionId);

    // Load the account
    BusinessAccount account = (BusinessAccount) accountDao.findById(accountId);
    Date now = new Date();
    Transaction transaction = new Transaction();
    transaction.setAmount(plan.getFee());
    transaction.setBuyer(account);
    transaction.setStatus(TransactionStatus.COMPLETE);
    transaction.setCurrency(Locale.US.getCountry());
    transaction.setOrderId(orderId);
    transaction.setTimeCreated(now);
    transaction.setTimeUpdated(now);

    Subscription subscription = new Subscription();
    subscription.setSubscriptionPlan(plan);
    subscription.setAccount(account);
    subscription.setTransaction(transaction);
    subscription.setTimeUpdated(now);
    subscription.setTimeCreated(now);
    subscription.setStatus(SubscriptionStatus.ACTIVE);
    subscriptionDao.save(subscription);

    // Cancel the latest subscription
    Subscription latestSubscription = account.getLatestSubscription();
    if (latestSubscription == null) {
      return;
    }
    
    logger.info(String.format("Subscription id %d to be cancelled", latestSubscription.getSubscriptionId()));
    if (latestSubscription.getStatus() != SubscriptionStatus.CANCELLED) {
      latestSubscription.setStatus(SubscriptionStatus.CANCELLED);
      subscriptionDao.save(latestSubscription);
    }
    
    tweetNotificationBli.sendSubscriptionCompletionNotification(subscription, EmailTemplate.SUBSCRIPTION_NOTIFICATION);
    logger.info(String.format("Subscription id %d to be cancelled", latestSubscription.getSubscriptionId()));
  }

  @Override
  public List<SubscriptionPlan> getAllSubscriptionPlans() {
    List<SubscriptionPlan> subscriptions = subscriptionPlanDao.getAll();
    return subscriptions;
  }

  @Override
  public void cancelOrder(String orderId) {
    logger.info(String.format("About to cancel transaction for order %s", orderId));
    Transaction transaction = transactionDao.findByOrderId(orderId);
    BusinessAccount account = BusinessAccount.class.cast(transaction.getBuyer());
    Subscription subscription = account.getLatestSubscription();
    subscription.setStatus(SubscriptionStatus.CANCELLED);
    subscription.setTimeUpdated(new Date());
    subscriptionDao.save(subscription);
    logger.info(String.format("Subscription id %d cancelled", subscription.getSubscriptionId()));
  }
}
