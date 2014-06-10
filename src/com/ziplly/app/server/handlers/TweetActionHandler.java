package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.TweetBLI;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;

public class TweetActionHandler extends AbstractTweetActionHandler<TweetAction, TweetResult> {
	private TweetBLI tweetBli;
	private Logger logger = Logger.getLogger(TweetActionHandler.class.getName());

	@Inject
	public TweetActionHandler(Provider<EntityManager> entityManagerProvider,
	    AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli,
	    TweetBLI tweetBli) {
		super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
		this.tweetBli = tweetBli;
	}

	@Override
	public TweetResult doExecute(TweetAction action, ExecutionContext arg1) throws DispatchException {
		checkNotNull(action.getTweet());
		validateSession();

		Tweet tweet = new Tweet(action.getTweet());
		tweet.setSender(session.getAccount());
		
		Tweet savedTweet = tweetBli.sendTweet(tweet, session.getAccount());
		TweetResult result = new TweetResult();
		result.setTweet(EntityUtil.clone(savedTweet));
		return result;
	}

	@Override
	public Class<TweetAction> getActionType() {
		return TweetAction.class;
	}
}
