package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountHandlerUtil;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class ValidateLoginActionHandler extends AbstractAccountActionHandler<ValidateLoginAction, ValidateLoginResult>{

	@Inject
	public ValidateLoginActionHandler(AccountDAO accountDao, SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}
	
	@Override
	public ValidateLoginResult execute(ValidateLoginAction action,
			ExecutionContext ec) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException("Invalid argument to ValidateLoginActionHandler");
		}
		
		Account account = accountBli.validateLogin(action.getEmail(), action.getPassword());
		AccountDTO accountDto = AccountHandlerUtil.getAccountDTO(account);
		return new ValidateLoginResult(accountDto);
	}

	@Override
	public Class<ValidateLoginAction> getActionType() {
		return ValidateLoginAction.class;
	}

}
