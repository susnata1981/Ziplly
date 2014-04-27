package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.ResendEmailVerificationAction;
import com.ziplly.app.shared.ResendEmailVerificationResult;

public class ResendEmailVerificationActionHandler extends
    AbstractAccountActionHandler<ResendEmailVerificationAction, ResendEmailVerificationResult> {

	@Inject
	public ResendEmailVerificationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public ResendEmailVerificationResult doExecute(ResendEmailVerificationAction action,
	    ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getEmail());
		try {
			accountBli.resendEmailVerification(action.getEmail());
		} catch (NotFoundException ex) {
			throw ex;
		} catch (AccountExistsException ex) {
			throw ex;
		} catch (Exception e) {
			throw new InternalException();
		}

		return new ResendEmailVerificationResult();
	}

	@Override
	public Class<ResendEmailVerificationAction> getActionType() {
		return ResendEmailVerificationAction.class;
	}

}
