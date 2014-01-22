package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;

public class GetNeighborhoodDetailsActionHandler extends AbstractAccountActionHandler<GetNeighborhoodDetailsAction, GetNeighborhoodDetailsResult>{

	@Inject
	public GetNeighborhoodDetailsActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetNeighborhoodDetailsResult execute(GetNeighborhoodDetailsAction action, ExecutionContext arg1)
			throws DispatchException {
		
		validateSession();
		
		Long totalResidents = accountDao.findTotalAccountsByNeighborhood(EntityType.PERSONAL_ACCOUNT, 
				session.getAccount().getNeighborhood().getNeighborhoodId());
		
		Long totalBusinesses = accountDao.findTotalAccountsByNeighborhood(EntityType.BUSINESS_ACCOUNT, 
				session.getAccount().getNeighborhood().getNeighborhoodId());
		
		GetNeighborhoodDetailsResult result = new GetNeighborhoodDetailsResult();
		result.setTotalResidents(totalResidents.intValue());
		result.setTotalBusinesses(totalBusinesses.intValue());
		return result;
	}

	@Override
	public Class<GetNeighborhoodDetailsAction> getActionType() {
		return GetNeighborhoodDetailsAction.class;
	}

}
