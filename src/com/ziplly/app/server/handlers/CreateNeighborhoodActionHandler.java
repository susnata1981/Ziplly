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
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.CreateNeighborhoodAction;
import com.ziplly.app.shared.CreateNeighborhoodResult;

public class CreateNeighborhoodActionHandler extends
    AbstractAccountActionHandler<CreateNeighborhoodAction, CreateNeighborhoodResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public CreateNeighborhoodActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    NeighborhoodDAO neighborhoodDao) {
		super(accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public CreateNeighborhoodResult
	    execute(CreateNeighborhoodAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getNeighborhood());

		validateSession();

		if (session.getAccount().getRole() != Role.ADMINISTRATOR) {
			throw new AccessError();
		}

		Neighborhood n = new Neighborhood(action.getNeighborhood());

		if (n.getParentNeighborhood() != null) {
			NeighborhoodDTO parent =
			    neighborhoodDao.findById(n.getParentNeighborhood().getNeighborhoodId());
			n.setParentNeighborhood(new Neighborhood(parent));
		}

		neighborhoodDao.save(n);
		CreateNeighborhoodResult result = new CreateNeighborhoodResult();
		return result;
	}

	@Override
	public Class<CreateNeighborhoodAction> getActionType() {
		return CreateNeighborhoodAction.class;
	}

}
