package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.AdminBLI;

public abstract class AbstractAdminActionHandler<T extends Action<V>, V extends Result> extends
    AbstractTweetActionHandler<T, V> {
	protected AdminBLI adminBli;

	public AbstractAdminActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli,
	    AdminBLI adminBli) {
		
		super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
		this.adminBli = adminBli;
	}

}
