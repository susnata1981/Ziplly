package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public class UpdateAccountActionHandler extends AbstractAccountActionHandler<UpdateAccountAction, UpdateAccountResult>{

	@Inject
	public UpdateAccountActionHandler(
			AccountDAO accountDao,
			SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public UpdateAccountResult execute(UpdateAccountAction action,
			ExecutionContext ec) throws DispatchException {
		
		if (action == null || action.getAccount() == null) {
			throw new IllegalArgumentException();
		}
		Account account = new Account(action.getAccount());
		accountBli.updateAccount(account);
		AccountDTO result = new AccountDTO(account);
		return new UpdateAccountResult(result);
	}

	@Override
	public Class<UpdateAccountAction> getActionType() {
		return UpdateAccountAction.class;
	}

}
