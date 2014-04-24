package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.UpdateAccountLocationAction;
import com.ziplly.app.shared.UpdateAccountLocationResult;

public class UpdateAccountLocationActionHandler extends AbstractAccountActionHandler<UpdateAccountLocationAction, UpdateAccountLocationResult>{

	@Inject
	public UpdateAccountLocationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
  }

	@Override
  public UpdateAccountLocationResult
      doExecute(UpdateAccountLocationAction action, ExecutionContext arg1) throws DispatchException {
		
		validateSession();
		
		Preconditions.checkArgument(session.getAccount().getRole() == Role.ADMINISTRATOR);
		accountDao.updateLocation(action.getAccount(), action.getLocation());
		return new UpdateAccountLocationResult();
  }

	@Override
  public Class<UpdateAccountLocationAction> getActionType() {
		return UpdateAccountLocationAction.class;
  }
}
