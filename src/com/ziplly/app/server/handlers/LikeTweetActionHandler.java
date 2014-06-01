package com.ziplly.app.server.handlers;

import java.util.Date;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Love;
import com.ziplly.app.server.model.jpa.Tweet;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;

public class LikeTweetActionHandler extends
    AbstractAccountActionHandler<LikeTweetAction, LikeResult> {
	private LikeDAO likeDao;

	@Inject
	public LikeTweetActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    LikeDAO likeDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.likeDao = likeDao;
	}

	@Override
	public LikeResult doExecute(LikeTweetAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getTweetId() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		Love like = new Love();
		Tweet tweet = new Tweet();
		tweet.setTweetId(action.getTweetId());
		like.setTweet(tweet);
		Account a = session.getAccount();
		like.setAuthor(a);
		like.setTimeCreated(new Date());
		try {
			LoveDTO loveDto = likeDao.save(like);
			TweetDTO tweetDto = new TweetDTO();
			tweetDto.setTweetId(action.getTweetId());
			loveDto.setTweet(tweetDto);
			return new LikeResult(loveDto);
		} catch (DuplicateException ex) {
			throw ex;
		}
	}

	@Override
	public Class<LikeTweetAction> getActionType() {
		return LikeTweetAction.class;
	}
}
