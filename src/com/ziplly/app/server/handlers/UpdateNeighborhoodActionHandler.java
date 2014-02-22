package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.UpdateNeighborhoodAction;
import com.ziplly.app.shared.UpdateNeighborhoodResult;

public class UpdateNeighborhoodActionHandler extends AbstractAccountActionHandler<UpdateNeighborhoodAction, UpdateNeighborhoodResult>{

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public UpdateNeighborhoodActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, NeighborhoodDAO neighborhoodDao) {
		super(accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public UpdateNeighborhoodResult execute(UpdateNeighborhoodAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkNotNull(action.getNeighborhood());
		validateSession();
		
		if (session.getAccount().getRole() != Role.ADMINISTRATOR) {
			throw new AccessError();
		}
		
		neighborhoodDao.update(new Neighborhood(action.getNeighborhood()));
		UpdateNeighborhoodResult result = new UpdateNeighborhoodResult();
		return result;
	}

	@Override
	public Class<UpdateNeighborhoodAction> getActionType() {
		return UpdateNeighborhoodAction.class;
	}

}
