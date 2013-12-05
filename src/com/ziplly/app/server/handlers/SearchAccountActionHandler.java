package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AdminBLI;
import com.ziplly.app.shared.SearchAccountAction;
import com.ziplly.app.shared.SearchAccountResult;

public class SearchAccountActionHandler extends AbstractAdminActionHandler<SearchAccountAction, SearchAccountResult>{

	@Inject
	public SearchAccountActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			TweetDAO tweetDao, AccountBLI accountBli, AdminBLI adminBli) {
		super(accountDao, sessionDao, tweetDao, accountBli, adminBli);
	}

	@Override
	public SearchAccountResult execute(SearchAccountAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null || action.getCriteria() == null) {
			throw new IllegalArgumentException();
		}
		
		List<AccountDTO> accounts = adminBli.getAccounts(action.getStart(), action.getEnd(), action.getCriteria());
		Long totalAccounts = adminBli.getTotalAccounts(action.getCriteria());
		SearchAccountResult result = new SearchAccountResult();
		result.setAccounts(accounts);
		result.setTotalAccounts(totalAccounts.intValue());
		return result;
	}

	@Override
	public Class<SearchAccountAction> getActionType() {
		return SearchAccountAction.class;
	}

}
