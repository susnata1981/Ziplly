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
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;

public class GetTweetForUserActionHandler extends AbstractAccountActionHandler<GetTweetForUserAction, GetTweetForUserResult>{
	private TweetDAO tweetDao;
	
	@Inject
	public GetTweetForUserActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, TweetDAO tweetDao) {
		super(accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}

	@Override
	public GetTweetForUserResult execute(GetTweetForUserAction action,
			ExecutionContext arg1) throws DispatchException {
		
//		validateSession();
		
		List<TweetDTO> tweets = tweetDao.findTweetsByAccountId(
				action.getAccountId(), 
				action.getPage(), 
				action.getPageSize());
		GetTweetForUserResult result = new GetTweetForUserResult(tweets);
		return result;
	}

	@Override
	public Class<GetTweetForUserAction> getActionType() {
		return GetTweetForUserAction.class;
	}
}
