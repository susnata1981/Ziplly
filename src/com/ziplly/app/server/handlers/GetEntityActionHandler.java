package com.ziplly.app.server.handlers;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Neighborhood;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class GetEntityActionHandler extends
    AbstractAccountActionHandler<GetEntityListAction, GetEntityResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public GetEntityActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    NeighborhoodDAO neighborhoodDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public GetEntityResult
	    doExecute(GetEntityListAction action, ExecutionContext ctx) throws DispatchException {

		validateSession();
		if (action.getEntityType() == EntityType.PERSONAL_ACCOUNT) {
			return findPersonalAccounts(action);
		} else if (action.getEntityType() == EntityType.BUSINESS_ACCOUNT) {
			return findBusinessAccounts(action);
		}

		throw new IllegalArgumentException("Invalid entity type passed to GetEntityListActionHandler");
	}

	private GetEntityResult findBusinessAccounts(GetEntityListAction action) throws NotFoundException {
		GetEntityResult result = new GetEntityResult();
		List<AccountDTO> accounts = Lists.newArrayList();
		result.setEntityType(action.getEntityType());

		List<Neighborhood> neighborhoods;
		List<Long> allNeighborhoodIds = Lists.newArrayList();

		switch (action.getSearchType()) {
			case BY_ZIP:
				neighborhoods = neighborhoodDao.findByPostalCode(action.getZip());
				allNeighborhoodIds.addAll(getNeighbodhoodIdList(neighborhoods));
				break;
			case BY_NEIGHBORHOOD:
			default:
				allNeighborhoodIds.add(action.getNeighborhoodId());
				neighborhoods = neighborhoodDao.findAllDescendentNeighborhoods(action.getNeighborhoodId());
				allNeighborhoodIds.addAll(getNeighbodhoodIdList(neighborhoods));
		}

		accounts.addAll(accountDao.findAccountsByNeighborhoods(
		    action.getEntityType(),
		    allNeighborhoodIds,
		    action.getPage(),
		    action.getPageSize()));

		if (action.isNeedTotalEntityCount()) {
			Long count =
			    accountDao
			        .getTotalAccountCountByNeighborhoods(action.getEntityType(), allNeighborhoodIds);

			result.setEntityCount(count);
		}
		result.setAccounts(accounts);
		return result;
	}

	private GetEntityResult findPersonalAccounts(GetEntityListAction action) throws NotFoundException {
		GetEntityResult result = new GetEntityResult();
		result.setEntityType(action.getEntityType());
		List<AccountDTO> accounts = Lists.newArrayList();

		switch (action.getSearchType()) {
			case BY_GENDER:
			default:
				accounts.addAll(accountDao.findPersonalAccounts(
				    action.getGender(),
				    action.getNeighborhoodId(),
				    action.getPage(),
				    action.getPageSize()));
		}
		result.setAccounts(accounts);

		if (action.isNeedTotalEntityCount()) {
			Long count =
			    accountDao.getTotalPersonalAccountCountByGender(
			        action.getGender(),
			        action.getNeighborhoodId());
			result.setEntityCount(count);
		}
		result.setAccounts(accounts);

		return result;
	}

	@Override
	public Class<GetEntityListAction> getActionType() {
		return GetEntityListAction.class;
	}

	private List<Long> getNeighbodhoodIdList(List<Neighborhood> neighborhoods) {
		if (neighborhoods.size() == 0) {
			return Collections.emptyList();
		}

		return Lists.transform(neighborhoods, new Function<Neighborhood, Long>() {

			@Override
			public Long apply(Neighborhood n) {
				return n.getNeighborhoodId();
			}
		});

	}
}
