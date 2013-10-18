package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class LogoutActionHandler extends AbstractAccountActionHandler<LogoutAction, LogoutResult>{
//	private Logger logger = Logger.getLogger(LogoutActionHandler.class.getCanonicalName());
	
	@Inject
	public LogoutActionHandler(AccountDAO accountDao,SessionDAO sessionDao,AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}
	
	@Override
	public LogoutResult execute(LogoutAction action, ExecutionContext ec)
			throws DispatchException {
		
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
