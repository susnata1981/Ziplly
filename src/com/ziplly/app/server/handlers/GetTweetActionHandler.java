package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AdminBLI;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.GetTweetsResult;

public class GetTweetActionHandler extends AbstractTweetActionHandler<GetTweetsAction, GetTweetsResult>{

	private AdminBLI adminBli;

	@Inject
	public GetTweetActionHandler(AccountDAO accountDao, SessionDAO sessionDao, TweetDAO tweetDao,
			AccountBLI accountBli, AdminBLI adminBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
		this.adminBli = adminBli;
	}

	@Override
	public GetTweetsResult execute(GetTweetsAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null || action.getCriteria() == null) {
			throw new IllegalArgumentException();
		}
		
		List<TweetDTO> tweets = adminBli.getTweets(action.getStart(), action.getEnd(), action.getCriteria());
		Long count = adminBli.getTotalTweetCount(action.getCriteria());
		
		GetTweetsResult result = new GetTweetsResult();
		result.setTweets(tweets);
		result.setTotalTweetCount(count);
		return result;
	}

	@Override
	public Class<GetTweetsAction> getActionType() {
		return GetTweetsAction.class;
	}
}
