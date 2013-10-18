package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookDetailsResult;

public class GetFacebookDetailsHandler extends AbstractAccountActionHandler<GetFacebookDetailsAction, GetFacebookDetailsResult>{

	@Inject
	public GetFacebookDetailsHandler(AccountDAO accountDao, SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetFacebookDetailsResult execute(GetFacebookDetailsAction input,
			ExecutionContext context) throws DispatchException {
		
		if (input == null || input.getCode() == null) {
			throw new IllegalArgumentException();
		}
		
		AccountDTO account = accountBli.getFacebookDetails(input.getCode());
		return new GetFacebookDetailsResult(account);
	}

	@Override
	public Class<GetFacebookDetailsAction> getActionType() {
		return GetFacebookDetailsAction.class;
	}
}
