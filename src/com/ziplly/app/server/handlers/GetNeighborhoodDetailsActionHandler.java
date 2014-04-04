package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;

public class GetNeighborhoodDetailsActionHandler extends
    AbstractAccountActionHandler<GetNeighborhoodDetailsAction, GetNeighborhoodDetailsResult> {

	@Inject
	public GetNeighborhoodDetailsActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public GetNeighborhoodDetailsResult doExecute(GetNeighborhoodDetailsAction action,
	    ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getNeighborhoodId());
		validateSession();

		Long totalResidents =
		    accountDao.findTotalAccountsByNeighborhood(
		        EntityType.PERSONAL_ACCOUNT,
		        action.getNeighborhoodId());

		Long totalBusinesses =
		    accountDao.findTotalAccountsByNeighborhood(
		        EntityType.BUSINESS_ACCOUNT,
		        action.getNeighborhoodId());

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
