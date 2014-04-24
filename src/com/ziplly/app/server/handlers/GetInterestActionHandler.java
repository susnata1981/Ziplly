package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetInterestAction;
import com.ziplly.app.shared.GetInterestResult;

public class GetInterestActionHandler extends
    AbstractAccountActionHandler<GetInterestAction, GetInterestResult> {
	private InterestDAO interestDao;

	@Inject
	public GetInterestActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    InterestDAO interestDao) {

		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.interestDao = interestDao;
	}

	@Override
	public GetInterestResult
	    doExecute(GetInterestAction action, ExecutionContext arg1) throws DispatchException {

		validateSession();
		List<InterestDTO> interests = interestDao.findAll();
		GetInterestResult response = new GetInterestResult();
		response.setInterests(interests);
		return response;
	}

	@Override
	public Class<GetInterestAction> getActionType() {
		return GetInterestAction.class;
	}
}
