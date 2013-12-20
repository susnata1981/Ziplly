package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetResidentsRequest;
import com.ziplly.app.shared.GetResidentsResult;

public class GetResidentsActionHandler extends AbstractAccountActionHandler<GetResidentsRequest, GetResidentsResult>{
	
	@Inject
	public GetResidentsActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetResidentsResult execute(GetResidentsRequest action,
			ExecutionContext ctx) throws DispatchException {
		
		if (action == null || action.getAccount() == null) {
			throw new IllegalArgumentException();
		}
		
		List<PersonalAccountDTO> accountByZip = accountBli.getAccountByZip(action.getAccount());
		GetResidentsResult result = new GetResidentsResult();		
		result.setAccounts(accountByZip);
		return result;
	}

	@Override
	public Class<GetResidentsRequest> getActionType() {
		return GetResidentsRequest.class;
	}

}
