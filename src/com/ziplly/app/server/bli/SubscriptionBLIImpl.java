package com.ziplly.app.server.bli;

import static com.google.common.base.Preconditions.checkArgument;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SubscriptionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Subscription;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public class SubscriptionBLIImpl implements SubscriptionBLI {
	private SubscriptionPlanDAO subscriptionPlanDao;
	private SubscriptionDAO subscriptionDao;
	private PaymentService paymentService;
	private Logger logger = Logger.getLogger(SubscriptionBLIImpl.class.getName());
	private AccountDAO accountDao;
	
	@Inject
	public SubscriptionBLIImpl(
			AccountDAO accountDao,
			SubscriptionPlanDAO subscriptionPlanDao,
	    SubscriptionDAO subscriptionDao,
	    PaymentService paymentService) {
		this.accountDao = accountDao;
		this.subscriptionPlanDao = subscriptionPlanDao;
		this.subscriptionDao = subscriptionDao;
		this.paymentService = paymentService;
	}

	@Override
	public SubscriptionEligibilityStatus checkSellerEligibility(Account account) {
		List<Subscription> subscriptions =
		    subscriptionDao.findByAccountId(account.getAccountId());
		
		if (subscriptions.size() == 0) {
			return SubscriptionEligibilityStatus.ELIGIBLE;
		}
		
		if (subscriptions.size() == 1) {
			SubscriptionEligibilityStatus status = (subscriptions.get(0).getStatus() != SubscriptionStatus.ACTIVE) 
					? SubscriptionEligibilityStatus.ELIGIBLE : SubscriptionEligibilityStatus.INELIGIBLE;
			return status;
		}
		
		int count = subscriptions.size();
		Subscription secondLatestSubscription = subscriptions.get(count - 2);
		Subscription latestSubscription = subscriptions.get(count - 1);
		
		checkArgument(secondLatestSubscription.getStatus() == SubscriptionStatus.ACTIVE);
		checkArgument(latestSubscription.getStatus() == SubscriptionStatus.CANCELLED);

		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(secondLatestSubscription.getTimeCreated());
		c.add(Calendar.DAY_OF_MONTH, 30);
		Date subscriptionEndDate = c.getTime();
		
		if (subscriptionEndDate.after(now)) {
			return SubscriptionEligibilityStatus.ACTIVE_SUBSCRIPTION;
		}
		return SubscriptionEligibilityStatus.ELIGIBLE;
	}

	@Override
  public String getJwtToken(Long accountId, Long subscriptionId) throws InternalException {
		SubscriptionPlan plan = subscriptionPlanDao.get(subscriptionId);
		try {
	    return paymentService.generateSubscriptionToken(accountId, subscriptionId, plan.getFee());
    } catch (InvalidKeyException e) {
    	logger.severe(String.format("Failed to generate token %s", e));
    	throw new InternalException(String.format("Failed to initiate pay"));
    } catch (SignatureException e) {
    	logger.severe(String.format("Failed to generate token %s", e));
    	throw new InternalException(String.format("Failed to initiate pay"));
    }
  }

	@Override
	public void completeTransaction(Long accountId, Long subscriptionId) throws NotFoundException {
		// Load the plan
		SubscriptionPlan plan = subscriptionPlanDao.get(subscriptionId);
		
		// Load the account
		Account account = accountDao.findById(accountId);
		Date now = new Date();
		Transaction transaction = new Transaction();
		transaction.setAmount(plan.getFee());
		transaction.setBuyer(account);
		transaction.setStatus(TransactionStatus.COMPLETE);
		transaction.setCurrency(Locale.US.getCountry());
		transaction.setTimeCreated(now);
		transaction.setTimeUpdated(now);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionPlan(plan);
		subscription.setTransaction(transaction);
		subscription.setTimeUpdated(now);
		subscription.setTimeCreated(now);
		subscription.setStatus(SubscriptionStatus.ACTIVE);
		subscriptionDao.save(subscription);
	}

	@Override
  public List<SubscriptionPlan> getAllSubscriptionPlans() {
		List<SubscriptionPlan> subscriptions = subscriptionPlanDao.getAll();
		return subscriptions;
  }
}
