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
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.shared.GetJwtTokenAction;
import com.ziplly.app.shared.GetJwtTokenResult;

public class GetJwtTokenActionHandler extends AbstractAccountActionHandler<GetJwtTokenAction, GetJwtTokenResult>{
	PaymentService paymentService;
	SubscriptionPlanDAO subscriptionPlanDAO;

	@Inject
	public GetJwtTokenActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli,
			PaymentService paymentService, SubscriptionPlanDAO subscriptionPlanDAO) {
		super(accountDao, sessionDao, accountBli);
		this.paymentService = paymentService;
		this.subscriptionPlanDAO = subscriptionPlanDAO;
	}

	@Override
	public GetJwtTokenResult execute(GetJwtTokenAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		List<SubscriptionPlanDTO> subscriptionPlans = subscriptionPlanDAO.getAll();
		GetJwtTokenResult result = new GetJwtTokenResult();
		try {
			for(SubscriptionPlanDTO plan : subscriptionPlans) {
				String token = paymentService.getJWT(session.getAccount().getAccountId(), plan.getFee());
				result.addToken(plan, token);
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Class<GetJwtTokenAction> getActionType() {
		return GetJwtTokenAction.class;
	}
}
