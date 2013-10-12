package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.BCrypt;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class ValidateLoginActionHandler extends AbstractAccountActionHandler<ValidateLoginAction, ValidateLoginResult>{

	@Inject
	public ValidateLoginActionHandler(AccountDAO accountDao, SessionDAO sessionDao) {
		super(accountDao, sessionDao);
	}
	
	@Override
	public ValidateLoginResult execute(ValidateLoginAction action,
			ExecutionContext ec) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException("Invalid argument to ValidateLoginActionHandler");
		}
		
		Account account = null;
		try {
			account = accountDao.findByEmail(action.getEmail());
			String password = account.getPassword();
			boolean checkpw = BCrypt.checkpw(action.getPassword(), password);
			if (!checkpw) {
				throw new InvalidCredentialsException();
			}
		} catch (NotFoundException e) {
			throw e;
		}
		
		// login user
		Long uid = doLogin(account);
		AccountDTO result = new AccountDTO(account);
		result.setUid(uid);

		return new ValidateLoginResult(result);
	}

	@Override
	public Class<ValidateLoginAction> getActionType() {
		return ValidateLoginAction.class;
	}

}
