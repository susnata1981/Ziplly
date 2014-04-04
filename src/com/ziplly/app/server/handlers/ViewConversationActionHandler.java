package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.ViewConversationAction;
import com.ziplly.app.shared.ViewConversationResult;

public class ViewConversationActionHandler extends
    AbstractAccountActionHandler<ViewConversationAction, ViewConversationResult> {
	private ConversationDAO conversationDao;

	@Inject
	public ViewConversationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    ConversationDAO conversationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
	}

	@Override
	public ViewConversationResult
	    doExecute(ViewConversationAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getConversationId() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		conversationDao.markConversationAsRead(action.getConversationId());
		return new ViewConversationResult();
	}

	@Override
	public Class<ViewConversationAction> getActionType() {
		return ViewConversationAction.class;
	}

}
