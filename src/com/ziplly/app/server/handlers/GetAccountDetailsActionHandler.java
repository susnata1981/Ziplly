package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class GetAccountDetailsActionHandler extends AbstractAccountActionHandler<GetAccountDetailsAction, GetAccountDetailsResult>{

	private ConversationDAO conversationDao;

	@Inject
	public GetAccountDetailsActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, ConversationDAO conversationDao) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
	}

	@Override
	public GetAccountDetailsResult execute(GetAccountDetailsAction action,
			ExecutionContext arg1) throws DispatchException {
		
		validateSession();
		
		Long accountId = session.getAccount().getAccountId();
		
		Long unreadMessageCount = conversationDao.getUnreadConversationForAccount(accountId);
		GetAccountDetailsResult result = new GetAccountDetailsResult();
		result.setUnreadMessages(unreadMessageCount.intValue());
		return result;
	}

	@Override
	public Class<GetAccountDetailsAction> getActionType() {
		return GetAccountDetailsAction.class;
	}

}
