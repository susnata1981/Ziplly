package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Role;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class UpdateTweetActionHandler extends
    AbstractAccountActionHandler<UpdateTweetAction, UpdateTweetResult> {
	private TweetDAO tweetDao;

	@Inject
	public UpdateTweetActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    TweetDAO tweetDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}

	@Override
	public UpdateTweetResult
	    doExecute(UpdateTweetAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getTweet() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		if (!hasPermission(action.getTweet())) {
			throw new AccessError();
		}

		Tweet tweet = new Tweet(action.getTweet());
		// tweet.setSender(session.getAccount());

		try {
			TweetDTO tweetDto = tweetDao.update(tweet);
			UpdateTweetResult result = new UpdateTweetResult();
			result.setTweet(tweetDto);
			return result;
		} catch (NoResultException nre) {
			throw nre;
		}
	}

	private boolean hasPermission(TweetDTO tweet) {
		return (session.getAccount().getAccountId() == tweet.getSender().getAccountId())
		    || session.getAccount().getRole() == Role.ADMINISTRATOR;
	}

	@Override
	public Class<UpdateTweetAction> getActionType() {
		return UpdateTweetAction.class;
	}

}
