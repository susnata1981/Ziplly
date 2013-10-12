package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;

public class RegisterAccountActionHandler extends AbstractAccountActionHandler<RegisterAccountAction, RegisterAccountResult> {

	@Inject
	public RegisterAccountActionHandler(AccountDAO accountDao, SessionDAO sessionDao) {
		super(accountDao, sessionDao);
	}
	
	@Override
	public RegisterAccountResult execute(RegisterAccountAction action,
			ExecutionContext ec) throws DispatchException {
		
		if (action == null || action.getAccount()==null) {
			throw new IllegalArgumentException("Invalid argument to RegisterAccountResult");
		}
		
		Account account = new Account(action.getAccount());
		
		// check for existing account
		Account existingAccount = null;
		try {
			existingAccount = accountDao.findByEmail(account.getEmail());
		} catch(NotFoundException nfe) {
			
		}
		
		if (existingAccount != null) {
			throw new AccountExistsException("Account already exists with this email: "+existingAccount.getEmail());
		}
		
		// create account otherwise
		accountDao.save(account);
		
		// login user
		Long uid = doLogin(account);

		AccountDTO newAccount = new AccountDTO(account);
		newAccount.setUid(uid);
		RegisterAccountResult result = new RegisterAccountResult(newAccount);
		result.setUid(uid);
		return result;
	}

	@Override
	public Class<RegisterAccountAction> getActionType() {
		return RegisterAccountAction.class;
	}
}
