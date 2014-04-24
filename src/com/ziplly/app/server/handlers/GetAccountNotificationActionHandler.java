package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetAccountNotificationResult;

public class GetAccountNotificationActionHandler extends
    AbstractAccountActionHandler<GetAccountNotificationAction, GetAccountNotificationResult> {

	private AccountNotificationDAO accountNotificationDao;

	@Inject
	public GetAccountNotificationActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    AccountNotificationDAO accountNotificationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.accountNotificationDao = accountNotificationDao;
	}

	@Override
	public GetAccountNotificationResult doExecute(GetAccountNotificationAction action,
	    ExecutionContext arg1) throws DispatchException {

		validateSession();

		List<AccountNotificationDTO> notifications =
		    accountNotificationDao.findAccountNotificationByAccountId(session
		        .getAccount()
		        .getAccountId());

		GetAccountNotificationResult result = new GetAccountNotificationResult(notifications);
		return result;
	}

	@Override
	public Class<GetAccountNotificationAction> getActionType() {
		return GetAccountNotificationAction.class;
	}
}
