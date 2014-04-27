package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.UpdateNeighborhoodAction;
import com.ziplly.app.shared.UpdateNeighborhoodResult;

public class UpdateNeighborhoodActionHandler extends
    AbstractAccountActionHandler<UpdateNeighborhoodAction, UpdateNeighborhoodResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public UpdateNeighborhoodActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    NeighborhoodDAO neighborhoodDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public UpdateNeighborhoodResult
	    doExecute(UpdateNeighborhoodAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getNeighborhood());
		validateSession();

		if (session.getAccount().getRole() != Role.ADMINISTRATOR) {
			throw new AccessException();
		}

		NeighborhoodDTO neighborhood = action.getNeighborhood();
		if (neighborhood.getParentNeighborhood() != null) {
			Long parentNeighborhoodId = neighborhood.getParentNeighborhood().getNeighborhoodId();
			NeighborhoodDTO parent = neighborhoodDao.findById(parentNeighborhoodId);
			neighborhood.setParentNeighborhood(parent);
		}

		neighborhoodDao.update(new Neighborhood(neighborhood));
		UpdateNeighborhoodResult result = new UpdateNeighborhoodResult();
		return result;
	}

	@Override
	public Class<UpdateNeighborhoodAction> getActionType() {
		return UpdateNeighborhoodAction.class;
	}

}
