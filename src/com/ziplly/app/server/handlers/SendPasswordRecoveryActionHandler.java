package com.ziplly.app.server.handlers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.SendPasswordRecoveryEmailAction;
import com.ziplly.app.shared.SendPasswordRecoveryEmailResult;

public class SendPasswordRecoveryActionHandler extends
    AbstractAccountActionHandler<SendPasswordRecoveryEmailAction, SendPasswordRecoveryEmailResult> {

	@Inject
	public SendPasswordRecoveryActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public SendPasswordRecoveryEmailResult doExecute(SendPasswordRecoveryEmailAction action,
	    ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getEmail() == null) {
			throw new IllegalArgumentException();
		}

		try {
			accountBli.sendPasswordRecoveryEmail(action.getEmail());
		} catch (UnsupportedEncodingException e) {
			throw new InternalException("Internal error");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			throw new InternalException("Internal error");
		}

		return new SendPasswordRecoveryEmailResult();
	}

	@Override
	public Class<SendPasswordRecoveryEmailAction> getActionType() {
		return SendPasswordRecoveryEmailAction.class;
	}

}
