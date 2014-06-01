package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public class UpdateAccountActionHandler extends
    AbstractAccountActionHandler<UpdateAccountAction, UpdateAccountResult> {

	@Inject
	public UpdateAccountActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public UpdateAccountResult
	    doExecute(UpdateAccountAction action, ExecutionContext ec) throws DispatchException {

		if (action == null || action.getAccount() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		AccountDTO accountDto = action.getAccount();
		Account account = EntityUtil.convert(accountDto);

		AccountDTO result = accountBli.updateAccount(account);
		// accountBli.setCurrentLocation(result,
		// session.getLocation().getNeighborhood().getNeighborhoodId());
		result.setCurrentLocation(EntityUtil.clone(session.getLocation()));
		return new UpdateAccountResult(result);
	}

	@Override
	public Class<UpdateAccountAction> getActionType() {
		return UpdateAccountAction.class;
	}
}
