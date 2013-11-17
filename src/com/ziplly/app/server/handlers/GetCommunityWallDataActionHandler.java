package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class GetCommunityWallDataActionHandler
		extends
		AbstractTweetActionHandler<GetCommunityWallDataAction, GetCommunityWallDataResult> {

	@Inject
	public GetCommunityWallDataActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, TweetDAO tweetDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public GetCommunityWallDataResult execute(
			GetCommunityWallDataAction action, ExecutionContext arg1)
			throws DispatchException {

		validateSession();

		int zip = session.getAccount().getZip();
		TweetType type = action.getType();
		List<TweetDTO> tweets = null;
		long time1 = System.currentTimeMillis();
		System.out.println("Time:"+time1);
		
		if (type.equals(TweetType.ALL)) {
			tweets = tweetDao.findTweetsByZip(zip);
		} else {
			tweets = tweetDao.findTweetsByTypeAndZip(type, zip);
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("Time:"+time2);
		System.out.println("Time elapsed:"+(time2-time1));
		GetCommunityWallDataResult gcwdr = new GetCommunityWallDataResult();
		gcwdr.setTweets(tweets);
		return gcwdr;
	}

	@Override
	public Class<GetCommunityWallDataAction> getActionType() {
		return GetCommunityWallDataAction.class;
	}

}
