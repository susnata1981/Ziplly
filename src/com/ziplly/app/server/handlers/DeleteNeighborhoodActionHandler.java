package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.DeleteNeighborhoodAction;
import com.ziplly.app.shared.DeleteNeighborhoodResult;

public class DeleteNeighborhoodActionHandler extends
    AbstractAccountActionHandler<DeleteNeighborhoodAction, DeleteNeighborhoodResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public DeleteNeighborhoodActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    NeighborhoodDAO neighborhoodDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public DeleteNeighborhoodResult
	    doExecute(DeleteNeighborhoodAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getNeighborhoodId());
		validateSession();

		if (session.getAccount().getRole() != Role.ADMINISTRATOR) {
			throw new AccessError();
		}

		neighborhoodDao.delete(action.getNeighborhoodId());

		return new DeleteNeighborhoodResult();

	}

	@Override
	public Class<DeleteNeighborhoodAction> getActionType() {
		return DeleteNeighborhoodAction.class;
	}

}
