package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.PendingInvitationsDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.PendingInvitations;
import com.ziplly.app.shared.AddInvitationAction;
import com.ziplly.app.shared.AddInvitationResult;

public class AddInvitationActionHandler extends
    AbstractAccountActionHandler<AddInvitationAction, AddInvitationResult> {

	private PendingInvitationsDAO pendingInvitationDao;

	@Inject
	public AddInvitationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    PendingInvitationsDAO pendingInvitationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.pendingInvitationDao = pendingInvitationDao;
	}

	@Override
	public AddInvitationResult doExecute(AddInvitationAction action, ExecutionContext arg1) throws DispatchException {
		Preconditions.checkArgument(action != null && action.getEmail() != null);
		PendingInvitations pi = new PendingInvitations();
		pi.setEmail(action.getEmail());
		pi.setZip(action.getZip());
		pendingInvitationDao.save(pi);
		return new AddInvitationResult();
	}

	@Override
	public Class<AddInvitationAction> getActionType() {
		return AddInvitationAction.class;
	}

}
