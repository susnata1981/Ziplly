package com.ziplly.app.server.handlers;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AdminBLI;
import com.ziplly.app.server.TweetBLI;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.GetTweetsResult;

public class GetTweetActionHandler extends
    AbstractTweetActionHandler<GetTweetsAction, GetTweetsResult> {

	private AdminBLI adminBli;
	private TweetBLI tweetBli;

	@Inject
	public GetTweetActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli,
	    TweetBLI tweetBli,
	    AdminBLI adminBli) {
		super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
		this.tweetBli = tweetBli;
		this.adminBli = adminBli;
	}

	@Override
	public GetTweetsResult
	    doExecute(GetTweetsAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getCriteria() == null) {
			throw new IllegalArgumentException();
		}

		List<TweetDTO> tweets =
		    adminBli.getTweets(action.getStart(), action.getEnd(), action.getCriteria());
		Long count = adminBli.getTotalTweetCount(action.getCriteria());

		GetTweetsResult result = new GetTweetsResult();
		result.setTweets(tweets);
		result.setTotalTweetCount(count);
		return result;
	}

	@Override
	protected void postHandler(GetTweetsResult result) {
		try {
	    tweetBli.injectJwtToken(result.getTweets());
    } catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (SignatureException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
	
	@Override
	public Class<GetTweetsAction> getActionType() {
		return GetTweetsAction.class;
	}
}
