package com.ziplly.app.server.handlers;

import java.util.Map;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetTweetCategoryDetailsAction;
import com.ziplly.app.shared.GetTweetCategoryDetailsResult;

public class GetTweetCategoryDetailsActionHandler extends
    AbstractTweetActionHandler<GetTweetCategoryDetailsAction, GetTweetCategoryDetailsResult> {

	@Inject
	public GetTweetCategoryDetailsActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public GetTweetCategoryDetailsResult doExecute(GetTweetCategoryDetailsAction action,
	    ExecutionContext arg1) throws DispatchException {

		validateSession();

		Map<TweetType, Integer> tweetTypeCounts =
		    tweetDao.findTweetCategoryCounts(action.getNeighborhoodId());

		GetTweetCategoryDetailsResult result = new GetTweetCategoryDetailsResult();
		result.setTweetCounts(tweetTypeCounts);
		return result;
	}

	@Override
	public Class<GetTweetCategoryDetailsAction> getActionType() {
		return GetTweetCategoryDetailsAction.class;
	}

}
