package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;

public class DeleteTweetActionHandler extends AbstractTweetActionHandler<DeleteTweetAction, DeleteTweetResult>{

	@Inject
	public DeleteTweetActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, TweetDAO tweetDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public DeleteTweetResult execute(DeleteTweetAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null || action.getTweetId() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		Tweet tweet = tweetDao.findTweetById(action.getTweetId());
		if (session.getAccount().getAccountId() != tweet.getSender().getAccountId()) {
			throw new AccessError();
		}
		
		tweetDao.delete(tweet);
		
		return new DeleteTweetResult();
	}

	@Override
	public Class<DeleteTweetAction> getActionType() {
		return DeleteTweetAction.class;
	}
}
