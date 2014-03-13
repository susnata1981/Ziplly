package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.AccountBLI;

public abstract class AbstractTweetActionHandler<T extends Action<R>, R extends Result> extends
    AbstractAccountActionHandler<T, R> {
	protected TweetDAO tweetDao;

	public AbstractTweetActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}
}
