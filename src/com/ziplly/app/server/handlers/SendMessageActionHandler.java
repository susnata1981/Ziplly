package com.ziplly.app.server.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.Message;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class SendMessageActionHandler extends AbstractAccountActionHandler<SendMessageAction, SendMessageResult> {
	private ConversationDAO conversationDao;
	private EmailService emailService;

	@Inject
	public SendMessageActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, ConversationDAO conversationDao, EmailService emailService) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
		this.emailService = emailService;
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
		
		// check notification settings and send email
		notifyUser(conversation.getReceiver(), conversation.getSender());
		SendMessageResult result = new SendMessageResult();
		return result;
	}

	private void notifyUser(Account recipient, Account sender) {
		Set<AccountNotificationSettings> notificationSettings = recipient.getNotificationSettings();
		for(AccountNotificationSettings notificationSetting : notificationSettings) {
			if (notificationSetting.getType() == NotificationType.PERSONAL_MESSAGE &&
					notificationSetting.getAction() == NotificationAction.EMAIL) {
				// send email in that case
				Map<String, String> data = new HashMap<String, String>();
				data.put(StringConstants.SENDER_NAME_KEY, sender.getName());
				data.put(StringConstants.RECIPIENT_EMAIL, sender.getEmail());
				data.put(StringConstants.RECIPIENT_NAME_KEY, recipient.getName());
				emailService.sendEmail(data, EmailTemplate.PENDING_MESSAGE);
			}
		}
	}

	@Override
	public Class<SendMessageAction> getActionType() {
		return SendMessageAction.class;
	}
}
