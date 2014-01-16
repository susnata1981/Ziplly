package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;

public class GetConversationActionHandler extends AbstractAccountActionHandler<GetConversationsAction, GetConversationsResult>{
	ConversationDAO conversationDao;

	@Inject
	public GetConversationActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, ConversationDAO conversationDao) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
	}

	@Override
	public GetConversationsResult execute(GetConversationsAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		List<ConversationDTO> conversations = Lists.newArrayList();
		if (action.getType() != ConversationType.SINGLE) {
			conversations.addAll(conversationDao.getConversationForAccount(
				session.getAccount().getAccountId(),
				action.getType(),
				action.getStart(),
				action.getPageSize()));
		} else {
			conversations.add(conversationDao.findConversationById(action.getConversationId()));
		}
		
		GetConversationsResult result = new GetConversationsResult();
		for(ConversationDTO c : conversations) {
			result.getConversations().add(c);
		}
		
		if (action.isGetTotalConversation()) {
			Long total = conversationDao.getTotalConversationCount(session.getAccount().getAccountId());
			result.setTotalConversations(total);
		}
		return result;
	}

	@Override
	public Class<GetConversationsAction> getActionType() {
		return GetConversationsAction.class;
	}
}
