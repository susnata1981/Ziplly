package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetNewMemberAction;
import com.ziplly.app.shared.GetNewMemberResult;

public class GetNewMembersActionHandler extends AbstractAccountActionHandler<GetNewMemberAction, GetNewMemberResult> {

	@Inject
	public GetNewMembersActionHandler(AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli) {
	  super(accountDao, sessionDao, accountBli);
  }

	@Override
  public GetNewMemberResult
      execute(GetNewMemberAction action, ExecutionContext arg1) throws DispatchException {

		int daysLookback = action.getDaysLookback();
		
		validateSession();
		
		List<AccountDTO> accounts = accountDao.getAccountCreatedWithin(daysLookback, action.getNeighborhoodId(), action.getEntityType());
		GetNewMemberResult result = new GetNewMemberResult();
		result.setAccounts(accounts);
		return result;
  }

	@Override
  public Class<GetNewMemberAction> getActionType() {
		return GetNewMemberAction.class;
  }

}
