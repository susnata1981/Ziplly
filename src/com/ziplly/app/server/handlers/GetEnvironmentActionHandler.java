package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetEnvironmentAction;
import com.ziplly.app.shared.GetEnvironmentResult;

public class GetEnvironmentActionHandler extends
    AbstractAccountActionHandler<GetEnvironmentAction, GetEnvironmentResult> {

	@Inject
	public GetEnvironmentActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetEnvironmentResult
	    execute(GetEnvironmentAction action, ExecutionContext arg1) throws DispatchException {

		GetEnvironmentResult result = new GetEnvironmentResult();
		result.setEnvironment(accountBli.getEnvironment());
		return result;
	}

	@Override
	public Class<GetEnvironmentAction> getActionType() {
		return GetEnvironmentAction.class;
	}
}
