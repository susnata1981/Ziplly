package com.ziplly.app.server.handlers;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.shared.SendEmailAction;
import com.ziplly.app.shared.SendEmailResult;

public class SendEmailActionHandler extends AbstractAccountActionHandler<SendEmailAction, SendEmailResult>{
	private EmailService emailService;

	@Inject
	public SendEmailActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, EmailService emailService) {
		super(accountDao, sessionDao, accountBli);
		this.emailService = emailService;
	}

	@Override
	public SendEmailResult execute(SendEmailAction action, ExecutionContext arg1)
			throws DispatchException {
		if (action == null || action.getEmails() == null || action.getEmails().size() == 0 || action.getData() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		Map<String, String> data = new HashMap<String, String>();
		for(String email : action.getEmails()) {
			data.clear();
			data.put(StringConstants.SENDER_NAME_KEY, session.getAccount().getName());
			data.put(StringConstants.RECIPIENT_NAME_KEY, "");
			data.put(StringConstants.RECIPIENT_EMAIL, email);
			emailService.sendEmail(data, action.getEmailTemplate());
		}
		return new SendEmailResult();
	}

	@Override
	public Class<SendEmailAction> getActionType() {
		return SendEmailAction.class;
	}
	
}