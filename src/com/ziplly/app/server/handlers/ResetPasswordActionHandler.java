package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.ResetPasswordResult;

public class ResetPasswordActionHandler extends
    AbstractAccountActionHandler<ResetPasswordAction, ResetPasswordResult> {

	@Inject
	public ResetPasswordActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public ResetPasswordResult
	    execute(ResetPasswordAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getPassword() == null) {
			throw new IllegalArgumentException();
		}

		accountBli.resetPassword(action.getAccountId(), action.getPassword());

		return new ResetPasswordResult();
	}

	@Override
	public Class<ResetPasswordAction> getActionType() {
		return ResetPasswordAction.class;
	}

}
