package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.server.EmailServiceImpl;
import com.ziplly.app.shared.SendEmailAction;
import com.ziplly.app.shared.SendEmailResult;

public class SendEmailActionHandler extends
    AbstractAccountActionHandler<SendEmailAction, SendEmailResult> {
	private EmailService emailService;

	@Inject
	public SendEmailActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    EmailService emailService) {
		super(accountDao, sessionDao, accountBli);
		this.emailService = emailService;
	}

	@Override
	public SendEmailResult
	    execute(SendEmailAction action, ExecutionContext arg1) throws DispatchException {
		if (action == null || action.getEmails() == null || action.getEmails().size() == 0
		    || action.getData() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		String senderEmail = session.getAccount().getEmail();
		for (String email : action.getEmails()) {
			EmailServiceImpl.Builder builder = new EmailServiceImpl.Builder();
			builder
			    .setRecipientName("recipientName")
			    .setRecipientEmail(email)
			    .setEmailTemplate(action.getEmailTemplate())
			    .setSenderName(session.getAccount().getName())
			    .setSenderEmail(senderEmail);
			emailService.sendTemplatedEmailFromSender(builder);
		}
		return new SendEmailResult();
	}

	@Override
	public Class<SendEmailAction> getActionType() {
		return SendEmailAction.class;
	}

}