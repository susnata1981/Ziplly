package com.ziplly.app.server.handlers;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.AccountNotification;
import com.ziplly.app.shared.ViewNotificationAction;
import com.ziplly.app.shared.ViewNotificationResult;

public class ViewNotificationActionHandler extends
    AbstractAccountActionHandler<ViewNotificationAction, ViewNotificationResult> {

	private AccountNotificationDAO accountNotificationDao;

	@Inject
	public ViewNotificationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    AccountNotificationDAO accountNotificationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.accountNotificationDao = accountNotificationDao;
	}

	@Override
	public ViewNotificationResult
	    doExecute(ViewNotificationAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkArgument(
		    action.getAccountNotification() != null,
		    "Invalid argument to ViewNotificationActionHandler");

		validateSession();

		AccountNotification an = new AccountNotification(action.getAccountNotification());
		an.setReadStatus(ReadStatus.READ);
		an.setTimeUpdated(new Date());
		accountNotificationDao.update(an);

		List<AccountNotificationDTO> notifications =
		    accountNotificationDao.findAccountNotificationByAccountId(session
		        .getAccount()
		        .getAccountId());
		ViewNotificationResult result = new ViewNotificationResult();
		result.setAccountNotifications(notifications);
		return result;
	}

	@Override
	public Class<ViewNotificationAction> getActionType() {
		return ViewNotificationAction.class;
	}
}
