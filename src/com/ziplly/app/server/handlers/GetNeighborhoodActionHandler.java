package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;

public class GetNeighborhoodActionHandler extends
    AbstractAccountActionHandler<GetNeighborhoodAction, GetNeighborhoodResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public GetNeighborhoodActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    NeighborhoodDAO neighborhoodDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public GetNeighborhoodResult
	    doExecute(GetNeighborhoodAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkArgument(action != null);

		GetNeighborhoodResult result = new GetNeighborhoodResult();
		List<NeighborhoodDTO> neighborhoods = null;
		switch (action.getSearchType()) {
			case ALL:
				neighborhoods = neighborhoodDao.findAll();
				for (NeighborhoodDTO n : neighborhoods) {
					result.getNeighbordhoods().add(n);
				}
				return result;
			case BY_NEIGHBORHOOD:
				Preconditions.checkNotNull(action.getNeighborhood());
				NeighborhoodDTO neighborhood = neighborhoodDao.findOrCreateNeighborhood(action.getNeighborhood());
				result.getNeighbordhoods().add(neighborhood);
				return result;
			case BY_NEIGHBORHOOD_LOCALITY:
				Preconditions.checkNotNull(action.getNeighborhood());
				neighborhoods = neighborhoodDao.findNeighborhoodsByLocality(action.getNeighborhood());
				result.getNeighbordhoods().addAll(neighborhoods);
				return result;
			case BY_ZIP:
			default:
				neighborhoods = neighborhoodDao.findByPostalCode(action.getPostalCode());
				for (NeighborhoodDTO n : neighborhoods) {
					result.getNeighbordhoods().add(n);
				}
				return result;
		}
	}

	@Override
	public Class<GetNeighborhoodAction> getActionType() {
		return GetNeighborhoodAction.class;
	}
}
