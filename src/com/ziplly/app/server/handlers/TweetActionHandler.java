package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;

public class TweetActionHandler extends AbstractTweetActionHandler<TweetAction, TweetResult> {

	@Inject
	public TweetActionHandler(AccountDAO accountDao, 
			SessionDAO sessionDao,
			TweetDAO tweetDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public TweetResult execute(TweetAction action, ExecutionContext arg1)
			throws DispatchException {
		if (action == null) {
			throw new IllegalArgumentException();
		}
		validateSession();
		Tweet tweet = new Tweet(action.getTweet());
		tweetDao.save(tweet);
		TweetResult result = new TweetResult();
		return result;
	}

	@Override
	public Class<TweetAction> getActionType() {
		return TweetAction.class;
	}
}
