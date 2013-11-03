package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.Message;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class SendMessageActionHandler extends AbstractAccountActionHandler<SendMessageAction, SendMessageResult> {
	private ConversationDAO conversationDao;

	@Inject
	public SendMessageActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, ConversationDAO conversationDao) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
	}

	@Override
	public SendMessageResult execute(SendMessageAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null || action.getConversation() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		ConversationDTO conversationDto = action.getConversation();
		Conversation conversation = new Conversation(conversationDto);
		for(MessageDTO m : conversationDto.getMessages()) {
			conversation.getMessages().add(new Message(m));
		}
		conversationDao.save(conversation);
		
		SendMessageResult result = new SendMessageResult();
		return result;
	}

	@Override
	public Class<SendMessageAction> getActionType() {
		return SendMessageAction.class;
	}
}
