package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class GetAccountByIdActionHandler extends AbstractAccountActionHandler<GetAccountByIdAction, GetAccountByIdResult>{

	@Inject
	public GetAccountByIdActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetAccountByIdResult execute(GetAccountByIdAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null || action.getAccountId() == null) {
			throw new IllegalArgumentException();
		}
		
		GetAccountByIdResult result = new GetAccountByIdResult();
		try {
			AccountDTO account = accountBli.getAccountById(action.getAccountId());
			result.setAccount(account);
			return result;

		} catch (NotFoundException nfe) {
			return result;
		}
	}

	@Override
	public Class<GetAccountByIdAction> getActionType() {
		return GetAccountByIdAction.class;
	}
}
