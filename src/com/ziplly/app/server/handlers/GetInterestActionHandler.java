package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetInterestAction;
import com.ziplly.app.shared.GetInterestResult;

public class GetInterestActionHandler extends AbstractAccountActionHandler<GetInterestAction, GetInterestResult> {
	private InterestDAO interestDao;

	@Inject
	public GetInterestActionHandler(
			AccountDAO accountDao, 
			SessionDAO sessionDao,
			AccountBLI accountBli,
			InterestDAO interestDao) {
		
		super(accountDao, sessionDao, accountBli);
		this.interestDao = interestDao;
	}

	@Override
	public GetInterestResult execute(GetInterestAction action, ExecutionContext arg1)
			throws DispatchException {
		
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
