package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Account;
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
			Account account = accountBli.verifyPasswordRecoverLink(action.getHash());
			AccountDTO result = EntityUtil.convert(account);
			return new VerifyPasswordRecoveryHashResult(result);
		} catch (NoResultException nre) {
			throw new AccessException("IllegalAccess");
		}
	}

	@Override
	public Class<VerifyPasswordRecoveryHashAction> getActionType() {
		return VerifyPasswordRecoveryHashAction.class;
	}
}
