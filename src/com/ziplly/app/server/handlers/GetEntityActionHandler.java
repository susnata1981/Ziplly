package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class GetEntityActionHandler extends
		AbstractAccountActionHandler<GetEntityListAction, GetEntityResult> {

	@Inject
	public GetEntityActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetEntityResult execute(GetEntityListAction action, ExecutionContext ctx)
			throws DispatchException {

		validateSession();
		List<AccountDTO> accounts = Lists.newArrayList();
		GetEntityResult result = new GetEntityResult();
		switch (action.getEntityType()) {
		case BUSINESS_ACCOUNT:
			result.setEntityType(EntityType.BUSINESS_ACCOUNT);
			accounts.addAll(accountDao.findAccountsByNeighborhood(EntityType.BUSINESS_ACCOUNT,
					session.getAccount().getNeighborhood().getNeighborhoodId()));
			break;
		case PERSONAL_ACCOUNT:
		default:
			result.setEntityType(EntityType.PERSONAL_ACCOUNT);
			accounts.addAll(accountDao.findAccountsByNeighborhood(EntityType.PERSONAL_ACCOUNT,
					session.getAccount().getNeighborhood().getNeighborhoodId()));

		}

		result.setAccounts(accounts);
		return result;
	}

	@Override
	public Class<GetEntityListAction> getActionType() {
		return GetEntityListAction.class;
	}

}
