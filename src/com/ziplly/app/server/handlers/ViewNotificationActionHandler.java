package com.ziplly.app.server.handlers;

import java.util.Date;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.ReadStatus;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.ViewNotificationAction;
import com.ziplly.app.shared.ViewNotificationResult;

public class ViewNotificationActionHandler extends AbstractAccountActionHandler<ViewNotificationAction, ViewNotificationResult>{

	private AccountNotificationDAO accountNotificationDao;

	@Inject
	public ViewNotificationActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, AccountNotificationDAO accountNotificationDao) {
		super(accountDao, sessionDao, accountBli);
		this.accountNotificationDao = accountNotificationDao;
	}

	@Override
	public ViewNotificationResult execute(ViewNotificationAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action.getAccountNotification() != null, "Invalid argument to ViewNotificationActionHandler");
		
		AccountNotification an = new AccountNotification(action.getAccountNotification());
		an.setReadStatus(ReadStatus.READ);
		an.setTimeUpdated(new Date());
		accountNotificationDao.update(an);
		return new ViewNotificationResult();
	}

	@Override
	public Class<ViewNotificationAction> getActionType() {
		return ViewNotificationAction.class;
	}

}
