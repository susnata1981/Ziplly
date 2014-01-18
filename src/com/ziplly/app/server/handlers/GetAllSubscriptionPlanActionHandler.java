package com.ziplly.app.server.handlers;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;

public class GetAllSubscriptionPlanActionHandler extends AbstractAccountActionHandler<GetAllSubscriptionPlanAction, GetAllSubscriptionPlanResult>{
	private SubscriptionPlanDAO subscriptionPlanDao;
	private PaymentService paymentService;

	@Inject
	public GetAllSubscriptionPlanActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli,
			PaymentService paymentService,
			SubscriptionPlanDAO subscriptionPlanDao) {
		super(accountDao, sessionDao, accountBli);
		this.paymentService = paymentService;
		this.subscriptionPlanDao = subscriptionPlanDao;
	}

	@Override
	public GetAllSubscriptionPlanResult execute(
			GetAllSubscriptionPlanAction action, ExecutionContext arg1)
			throws DispatchException {
		
		validateSession();
		
		GetAllSubscriptionPlanResult result = new GetAllSubscriptionPlanResult();
		
		List<SubscriptionPlanDTO> plans = subscriptionPlanDao.getAll();
		for(SubscriptionPlanDTO plan : plans) {
			try {
				String token = paymentService.getJWT(session.getAccount().getAccountId(), plan.getFee());
				result.add(plan, token);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (SignatureException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public Class<GetAllSubscriptionPlanAction> getActionType() {
		return GetAllSubscriptionPlanAction.class;
	}

}
