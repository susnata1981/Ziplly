package com.ziplly.app.server.handlers;

import java.util.Collections;
import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;

public class GetEntityActionHandler extends
		AbstractAccountActionHandler<GetEntityListAction, GetEntityResult> {

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public GetEntityActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, NeighborhoodDAO neighborhoodDao) {
		super(accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public GetEntityResult execute(GetEntityListAction action, ExecutionContext ctx)
			throws DispatchException {

		validateSession();
		if (action.getEntityType() == EntityType.PERSONAL_ACCOUNT) {
			return findPersonalAccounts(action);
		} else if (action.getEntityType() == EntityType.BUSINESS_ACCOUNT) {
			return findBusinessAccounts(action);
		}
		
		throw new IllegalArgumentException("Invalid entity type passed to GetEntityListActionHandler");
	}

	private GetEntityResult findBusinessAccounts(GetEntityListAction action) {
		GetEntityResult result = new GetEntityResult();
		List<AccountDTO> accounts = Lists.newArrayList();
		result.setEntityType(action.getEntityType());

		List<NeighborhoodDTO> neighborhoods;
		// TODO
		switch (action.getSearchType()) {
		case BY_ZIP:
			neighborhoods = neighborhoodDao.findByPostalCode(action.getZip());
			break;
		default:
			neighborhoods = ImmutableList.of(EntityUtil.clone(session.getAccount()
					.getNeighborhood()));
		}

		if (neighborhoods.isEmpty()) {
			result.setAccounts(Collections.<AccountDTO> emptyList());
			return result;
		}

		accounts.addAll(accountDao.findAccountsByNeighborhoods(action.getEntityType(),
				neighborhoods, action.getPage(), action.getPageSize()));

		if (action.isNeedTotalEntityCount()) {
			Long count = accountDao.getTotalAccountCountByNeighborhoods(action.getEntityType(),
					neighborhoods);
			result.setEntityCount(count);
		}
		result.setAccounts(accounts);
		return result;
	}

	private GetEntityResult findPersonalAccounts(GetEntityListAction action) {
		GetEntityResult result = new GetEntityResult();
		result.setEntityType(action.getEntityType());
		List<AccountDTO> accounts = Lists.newArrayList();

		switch (action.getSearchType()) {
		case BY_GENDER:
		default:
			accounts.addAll(accountDao.findPersonalAccounts(
					action.getGender(),
					session.getAccount().getNeighborhood().getNeighborhoodId(),
					action.getPage(),
					action.getPageSize()));
		}
		result.setAccounts(accounts);
		
		if (action.isNeedTotalEntityCount()) {
			Long count = accountDao.getTotalPersonalAccountCountByGender(
					action.getGender(),
					session.getAccount().getNeighborhood().getNeighborhoodId());
			result.setEntityCount(count);
		}
		result.setAccounts(accounts);
		
		return result;
	}

	@Override
	public Class<GetEntityListAction> getActionType() {
		return GetEntityListAction.class;
	}

}
