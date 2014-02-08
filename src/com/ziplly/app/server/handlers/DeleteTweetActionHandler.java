package com.ziplly.app.server.handlers;

import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
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
		
		TweetDTO tweetDto;
		try {
		 tweetDto = tweetDao.findTweetById(action.getTweetId());
		} catch(NoResultException nre) {
			throw new NotFoundException();
		}
		
		AccountDTO sender = accountDao.findById(tweetDto.getSender().getAccountId());
		
		// TODO 
		// Need to load sender account separately before hasPermission call.
		if (!hasPermission(tweetDto.getSender())) {
			throw new AccessError();
		}

		Tweet tweet = new Tweet(tweetDto);
		tweetDao.delete(tweet);
		
		return new DeleteTweetResult();
	}

	private boolean hasPermission(AccountDTO sender) {
		return (session.getAccount().getAccountId() == sender.getAccountId())
				|| session.getAccount().getRole() == Role.ADMINISTRATOR;
	}

	@Override
	public Class<DeleteTweetAction> getActionType() {
		return DeleteTweetAction.class;
	}
}
