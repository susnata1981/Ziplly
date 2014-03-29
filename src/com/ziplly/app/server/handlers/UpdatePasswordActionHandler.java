package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class UpdatePasswordActionHandler extends
    AbstractAccountActionHandler<UpdatePasswordAction, UpdatePasswordResult> {

	@Inject
	public UpdatePasswordActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public UpdatePasswordResult
	    doExecute(UpdatePasswordAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getOldPassword() == null || action.getNewPassword() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		Account account = session.getAccount();
		accountBli.updatePassword(account, action.getOldPassword(), action.getNewPassword());

		return new UpdatePasswordResult();
	}

	@Override
	public Class<UpdatePasswordAction> getActionType() {
		return UpdatePasswordAction.class;
	}

}
