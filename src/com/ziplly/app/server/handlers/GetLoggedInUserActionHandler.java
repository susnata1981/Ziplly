package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public class GetLoggedInUserActionHandler extends
		AbstractAccountActionHandler<GetLoggedInUserAction, GetLoggedInUserResult> {

	@Inject
	public GetLoggedInUserActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetLoggedInUserResult execute(GetLoggedInUserAction action,
			ExecutionContext ec) throws DispatchException {
		
		try {
			AccountDTO account = accountBli.getLoggedInUser();
			if (account == null) {
				return new GetLoggedInUserResult(null);
			}
			return new GetLoggedInUserResult(account);
		} catch (NotFoundException nfe) {
			return new GetLoggedInUserResult(null);
		}
	}

	@Override
	public Class<GetLoggedInUserAction> getActionType() {
		return GetLoggedInUserAction.class;
	}
}
