package com.ziplly.app.client.view.handler;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.server.handlers.AbstractAccountActionHandler;
import com.ziplly.app.shared.GetJwtTokenAction;
import com.ziplly.app.shared.GetJwtTokenResult;

public class GetJwtTokenActionHandler extends AbstractAccountActionHandler<GetJwtTokenAction, GetJwtTokenResult>{
	PaymentService paymentService;

	@Inject
	public GetJwtTokenActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli,
			PaymentService paymentService) {
		super(accountDao, sessionDao, accountBli);
		this.paymentService = paymentService;
	}

	@Override
	public GetJwtTokenResult execute(GetJwtTokenAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		GetJwtTokenResult result = new GetJwtTokenResult();
		try {
			String token = paymentService.getJWT(session.getAccount().getAccountId(), 3.00);
			result.setToken(token);
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
