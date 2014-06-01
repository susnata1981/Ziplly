package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.LocationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Location;
import com.ziplly.app.shared.SwitchLocationAction;
import com.ziplly.app.shared.SwitchLocationResult;

public class SwitchLocationActionHander extends
    AbstractAccountActionHandler<SwitchLocationAction, SwitchLocationResult> {
	private LocationDAO locationDao;

	@Inject
	public SwitchLocationActionHander(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    LocationDAO locationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.locationDao = locationDao;
	}

	@Override
	public SwitchLocationResult
	    doExecute(SwitchLocationAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkNotNull(action.getLocationId());

		validateSession();

		Account loggedInAccount = session.getAccount();
		if (!loggedInAccount.getLocations().contains(session.getLocation())) {
			throw new AccessException(String.format("Unauthorized to switch locations"));
		}

		Location location = locationDao.findById(action.getLocationId());
		session.setLocation(location);
		sessionDao.save(session);

		SwitchLocationResult result = new SwitchLocationResult();
		AccountDTO account = EntityUtil.convert(loggedInAccount);
		account.setCurrentLocation(EntityUtil.clone(session.getLocation()));
		account.setUid(session.getUid());
		result.setAccount(account);
		return result;
	}

	@Override
	public Class<SwitchLocationAction> getActionType() {
		return SwitchLocationAction.class;
	}

}
