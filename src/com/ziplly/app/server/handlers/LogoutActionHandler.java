package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class LogoutActionHandler extends AbstractAccountActionHandler<LogoutAction, LogoutResult> {
	// private Logger logger =
	// Logger.getLogger(LogoutActionHandler.class.getCanonicalName());

	@Inject
	public LogoutActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao, 
			SessionDAO sessionDao, 
			AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public LogoutResult doExecute(LogoutAction action, ExecutionContext ec) throws DispatchException {

		if (action == null || action.getUid() == null) {
			throw new IllegalArgumentException();
		}

		accountBli.logout(action.getUid());
		return new LogoutResult();
	}

	@Override
	public Class<LogoutAction> getActionType() {
		return LogoutAction.class;
	}
}
