package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AdminBLI;

public abstract class AbstractAdminActionHandler<T extends Action<V>, V extends Result> extends AbstractTweetActionHandler<T,V>{
	protected AdminBLI adminBli;

	public AbstractAdminActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			TweetDAO tweetDao, AccountBLI accountBli, AdminBLI adminBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
		this.adminBli = adminBli;
	}

}
