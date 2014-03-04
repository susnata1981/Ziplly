package com.ziplly.app.server.handlers;

import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountRegistrationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.VerifyEmailAction;
import com.ziplly.app.shared.VerifyEmailResult;

public class VerifyEmailActionHandler extends AbstractAccountActionHandler<VerifyEmailAction, VerifyEmailResult> {
	private AccountRegistrationDAO accountRegistrationDao;

	@Inject
	public VerifyEmailActionHandler(
			AccountDAO accountDao, 
			SessionDAO sessionDao,
			AccountBLI accountBli,
			AccountRegistrationDAO accountRegistrationDao) {
		super(accountDao, sessionDao, accountBli);
		this.accountRegistrationDao = accountRegistrationDao;
	}

	@Override
	public VerifyEmailResult execute(VerifyEmailAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action.getCode() != null && action.getId() != null);
		
		try {
			accountRegistrationDao.findAndVerifyAccount(action.getId(), action.getCode());
			return new VerifyEmailResult();
			
		} catch(NoResultException nre) {
			throw new AccessError();
		}
	}

	@Override
	public Class<VerifyEmailAction> getActionType() {
		return VerifyEmailAction.class;
	}
}
