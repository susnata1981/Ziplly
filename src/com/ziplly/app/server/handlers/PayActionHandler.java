package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Date;
import java.util.Locale;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SubscriptionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.model.Subscription;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.PaymentService;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.PayResult;

public class PayActionHandler extends AbstractAccountActionHandler<PayAction, PayResult> {
	private SubscriptionPlanDAO subscriptionPlanDao;
	private PaymentService paymentService;
	private SubscriptionDAO subscriptionDao;

	@Inject
	public PayActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao, 
			SessionDAO sessionDao, 
			AccountBLI accountBli,
			SubscriptionDAO subscriptionDao,
			SubscriptionPlanDAO subscriptionPlanDao,
			PaymentService paymentService) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.subscriptionDao = subscriptionDao;
		this.subscriptionPlanDao = subscriptionPlanDao;
		this.paymentService = paymentService;
	}

	@Override
	public PayResult doExecute(PayAction action, ExecutionContext arg1) throws DispatchException {

		checkNotNull(action.getSubscriptionId());
		validateSession();

		SubscriptionPlan subscriptionPlan = subscriptionPlanDao.get(action.getSubscriptionId());
		try {
	    String token = paymentService.generateSubscriptionToken(session.getAccount().getAccountId(), subscriptionPlan.getFee());
    } catch (Exception e) {
    }
		
		Date now = new Date();
		Transaction transaction = new Transaction();
		transaction.setAmount(subscriptionPlan.getFee());
		transaction.setBuyer(session.getAccount());
		transaction.setCurrency(Locale.US.getCountry());
		transaction.setTimeCreated(now);
		transaction.setTimeUpdated(now);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionPlan(subscriptionPlan);
		subscription.setTransaction(transaction);
		subscription.setTimeUpdated(now);
		subscription.setTimeCreated(now);
		subscription.setStatus(SubscriptionStatus.ACTIVE);

		subscriptionDao.save(subscription);
		PayResult result = new PayResult();
		result.setSubscription(EntityUtil.clone(subscription));
		return result;
	}

	@Override
	public Class<PayAction> getActionType() {
		return PayAction.class;
	}

}