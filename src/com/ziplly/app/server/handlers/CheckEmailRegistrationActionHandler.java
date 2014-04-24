package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountRegistrationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountRegistration;
import com.ziplly.app.model.AccountRegistration.AccountRegistrationStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.CheckEmailRegistrationAction;
import com.ziplly.app.shared.CheckEmailRegistrationResult;

public class CheckEmailRegistrationActionHandler extends
    AbstractAccountActionHandler<CheckEmailRegistrationAction, CheckEmailRegistrationResult> {
	private AccountRegistrationDAO registrationDao;

	@Inject
	public CheckEmailRegistrationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    AccountRegistrationDAO registrationDao) {
		
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.registrationDao = registrationDao;
	}

	@Override
	public CheckEmailRegistrationResult doExecute(CheckEmailRegistrationAction action,
	    ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getEmail() == null) {
			throw new IllegalArgumentException();
		}
		AccountRegistration ar = null;
		try {
			ar = registrationDao.findByEmailAndCode(action.getEmail(), action.getCode());
			ar.setStatus(AccountRegistrationStatus.USED);
			registrationDao.update(ar);
		} catch (NoResultException nre) {
			throw new AccessError("You've not been invited yet.");
		}

		CheckEmailRegistrationResult result = new CheckEmailRegistrationResult();
		if (ar.getBusinessType() != null) {
			result.setBusinessType(ar.getBusinessType());
			result.setAccountType(ar.getAccountType());
		}
		return result;
	}

	@Override
	public Class<CheckEmailRegistrationAction> getActionType() {
		return CheckEmailRegistrationAction.class;
	}

}
