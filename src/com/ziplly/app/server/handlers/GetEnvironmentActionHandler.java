package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetEnvironmentAction;
import com.ziplly.app.shared.GetEnvironmentResult;

public class GetEnvironmentActionHandler extends
    AbstractAccountActionHandler<GetEnvironmentAction, GetEnvironmentResult> {

	@Inject
	public GetEnvironmentActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public GetEnvironmentResult
	    doExecute(GetEnvironmentAction action, ExecutionContext arg1) throws DispatchException {

		GetEnvironmentResult result = new GetEnvironmentResult();
		result.setEnvironment(accountBli.getEnvironment());
		return result;
	}

	@Override
	public Class<GetEnvironmentAction> getActionType() {
		return GetEnvironmentAction.class;
	}
}
