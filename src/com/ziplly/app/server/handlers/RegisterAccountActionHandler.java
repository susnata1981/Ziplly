package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountHandlerUtil;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;

public class RegisterAccountActionHandler
		extends
		AbstractAccountActionHandler<RegisterAccountAction, RegisterAccountResult> {

	@Inject
	public RegisterAccountActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public RegisterAccountResult execute(RegisterAccountAction action,
			ExecutionContext ec) throws DispatchException {

		if (action == null || action.getAccount() == null) {
			throw new IllegalArgumentException(
					"Invalid argument to RegisterAccountResult");
		}
		RegisterAccountResult result = new RegisterAccountResult();
		AccountDTO accountDto = action.getAccount();
		
//		if (accountDto instanceof PersonalAccountDTO) {
		Account account = AccountHandlerUtil.getAccount(accountDto);
			try {
				Account newAccount = accountBli.register(account);
				accountDto = AccountHandlerUtil.getAccountDTO(newAccount);
				result.setAccount(accountDto);
				result.setUid(accountDto.getUid());
				return result;
			} catch (AccountExistsException e) {
				throw e;
			}
//		}
	}

	@Override
	public Class<RegisterAccountAction> getActionType() {
		return RegisterAccountAction.class;
	}
}
