package com.ziplly.app.server.handlers;

import java.util.Date;
import java.util.Set;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.Message;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.TweetNotificationBLI;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class SendMessageActionHandler extends
    AbstractAccountActionHandler<SendMessageAction, SendMessageResult> {
	private ConversationDAO conversationDao;
	private AccountNotificationDAO accountNotificationDao;
	private TweetNotificationBLI tweetNotificationBli;

	@Inject
	public SendMessageActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    ConversationDAO conversationDao,
	    AccountNotificationDAO accountNotificationDao,
	    TweetNotificationBLI tweetNotificationBli) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
		this.accountNotificationDao = accountNotificationDao;
		this.tweetNotificationBli = tweetNotificationBli;
	}

	@Override
	public SendMessageResult
	    execute(SendMessageAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getConversation() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		ConversationDTO conversationDto = action.getConversation();
		Conversation conversation = new Conversation(conversationDto);
		for (MessageDTO m : conversationDto.getMessages()) {
			conversation.getMessages().add(new Message(m));
		}
		Conversation newConversation = conversationDao.save(conversation);

		// check notification settings and send email
		notifyUser(newConversation);

		SendMessageResult result = new SendMessageResult();
		return result;
	}

	// Needs to move to NotificationBLI
	private void notifyUser(Conversation conversation) {
		Account recipient = conversation.getReceiver();
		Account sender = conversation.getSender();

		// Email user
		Set<AccountNotificationSettings> notificationSettings = recipient.getNotificationSettings();
		for (AccountNotificationSettings notificationSetting : notificationSettings) {
			if (notificationSetting.getType() == NotificationType.PERSONAL_MESSAGE
			    && notificationSetting.getAction() == NotificationAction.EMAIL) {

				tweetNotificationBli.sendEmail(sender, recipient, EmailTemplate.PENDING_MESSAGE);
			}
		}

		updateAccountNotification(conversation);
	}

	private void updateAccountNotification(Conversation conversation) {
		// save account notification
		AccountNotification result =
		    accountNotificationDao.findAccountNotificationByConversationId(conversation.getId());
		if (result == null) {
			AccountNotification an = new AccountNotification();
			an.setRecipient(conversation.getReceiver());
			an.setSender(conversation.getSender());
			an.setConversation(conversation);
			an.setReadStatus(ReadStatus.UNREAD);
			an.setType(NotificationType.PERSONAL_MESSAGE);
			an.setConversation(conversation);
			an.setTimeCreated(new Date());
			an.setTimeUpdated(new Date());
			accountNotificationDao.save(an);
		} else {
			result.setReadStatus(ReadStatus.UNREAD);
			accountNotificationDao.save(result);
		}
	}

	@Override
	public Class<SendMessageAction> getActionType() {
		return SendMessageAction.class;
	}
}
