package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.AccountBLI;

public abstract class AbstractTweetActionHandler<T extends Action<R>, R extends Result> extends
    AbstractAccountActionHandler<T, R> {
	protected TweetDAO tweetDao;

	public AbstractTweetActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}
}
