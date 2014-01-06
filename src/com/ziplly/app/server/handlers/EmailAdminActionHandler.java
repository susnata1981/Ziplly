package com.ziplly.app.server.handlers;

import javax.mail.MessagingException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.EmailService;
import com.ziplly.app.shared.EmailAdminAction;
import com.ziplly.app.shared.EmailAdminResult;

public class EmailAdminActionHandler extends AbstractAccountActionHandler<EmailAdminAction, EmailAdminResult>{
	private static final String APP_ADMIN_EMAIL = "app.admin.email2";
	private EmailService emailService;

	@Inject
	public EmailAdminActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, EmailService emailService) {
		super(accountDao, sessionDao, accountBli);
		this.emailService = emailService;
	}

	@Override
	public EmailAdminResult execute(EmailAdminAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action.getContent() != null 
				&& action.getFrom() != null
				&& action.getSubject() != null,
				"Invalid argument to EmailAdminActionHandler");
	
		String to = System.getProperty(APP_ADMIN_EMAIL);
		try {
			emailService.sendEmail(action.getSubject(), action.getContent(), action.getFrom(), to);
		} catch (MessagingException e) {
		}
		
		return new EmailAdminResult();
	}

	@Override
	public Class<EmailAdminAction> getActionType() {
		return EmailAdminAction.class;
	}
}
