package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashAction;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashResult;

public class VerifyPasswordRecoveryHashActionHandler
    extends
    AbstractAccountActionHandler<VerifyPasswordRecoveryHashAction, VerifyPasswordRecoveryHashResult> {

	@Inject
	public VerifyPasswordRecoveryHashActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public VerifyPasswordRecoveryHashResult doExecute(VerifyPasswordRecoveryHashAction action,
	    ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getHash() == null) {
			throw new IllegalArgumentException();
		}

		try {
			AccountDTO account = accountBli.verifyPasswordRecoverLink(action.getHash());
			return new VerifyPasswordRecoveryHashResult(account);
		} catch (NoResultException nre) {
			throw new AccessError("IllegalAccess");
		}
	}

	@Override
	public Class<VerifyPasswordRecoveryHashAction> getActionType() {
		return VerifyPasswordRecoveryHashAction.class;
	}
}
