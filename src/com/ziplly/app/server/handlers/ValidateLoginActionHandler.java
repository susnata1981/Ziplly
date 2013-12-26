package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.EmailAction;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class ValidateLoginActionHandler extends AbstractAccountActionHandler<ValidateLoginAction, ValidateLoginResult>{

	@Inject
	public ValidateLoginActionHandler(AccountDAO accountDao, SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}
	
	@Override
	public ValidateLoginResult execute(ValidateLoginAction action,
			ExecutionContext ec) throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException("Invalid argument to ValidateLoginActionHandler");
		}
		AccountDTO account = accountBli.validateLogin(action.getEmail(), action.getPassword());
//		sendMail(account);
		return new ValidateLoginResult(account);
	}

	private void sendMail(AccountDTO account) {
		Queue queue = QueueFactory.getQueue(StringConstants.EMAIL_QUEUE_NAME);
		String backendAddress = BackendServiceFactory.getBackendService().getBackendAddress(System.getProperty(StringConstants.BACKEND_INSTANCE_NAME_1));
		TaskOptions options = TaskOptions.Builder.withUrl("/sendmail").method(Method.POST)
				.param("action", EmailAction.BY_ZIP.name())
//				.param("recipientEmail", account.getEmail())
//				.param("recipientName", account.getDisplayName())
				.param("zip", Integer.toString(account.getZip()))
				.param("emailTemplateId",EmailTemplate.WELCOME_REGISTRATION.name())
				.header("Host", backendAddress);
		queue.add(options);
	}

	@Override
	public Class<ValidateLoginAction> getActionType() {
		return ValidateLoginAction.class;
	}

}
