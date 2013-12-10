package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.HashtagDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class GetCommunityWallDataActionHandler
		extends
		AbstractTweetActionHandler<GetCommunityWallDataAction, GetCommunityWallDataResult> {

	private HashtagDAO hashtagDao;

	@Inject
	public GetCommunityWallDataActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, TweetDAO tweetDao, AccountBLI accountBli, HashtagDAO hashtagDao) {
		super(accountDao, sessionDao, tweetDao, accountBli);
		this.hashtagDao = hashtagDao;
	}

	@Override
	public GetCommunityWallDataResult execute(
			GetCommunityWallDataAction action, ExecutionContext arg1)
			throws DispatchException {

		validateSession();

		if (action.getSearchType() == GetCommunityWallDataAction.SearchType.CATEGORY) {
			return getTweetsByCategory(action);
		} else if (action.getSearchType() == GetCommunityWallDataAction.SearchType.HASHTAG) {
			return getTweetsByHashtag(action);
		}
		throw new IllegalArgumentException();
	}

	private GetCommunityWallDataResult getTweetsByHashtag(GetCommunityWallDataAction action) {
		long time1 = System.currentTimeMillis();
		System.out.println("Time:"+time1);
		
		List<TweetDTO> tweets = hashtagDao.getTweetsForTag(action.getHashtag(), action.getPage(), action.getPageSize());
		
		long time2 = System.currentTimeMillis();
		System.out.println("Time:"+time2);
		System.out.println("Time elapsed:"+(time2-time1));
		GetCommunityWallDataResult gcwdr = new GetCommunityWallDataResult();
		gcwdr.setTweets(tweets);
		return gcwdr;
	}

	private GetCommunityWallDataResult getTweetsByCategory(GetCommunityWallDataAction action) {
		int zip = session.getAccount().getZip();
		TweetType type = action.getType();
		List<TweetDTO> tweets = null;
		long time1 = System.currentTimeMillis();
		System.out.println("Time:"+time1);
		
		if (type.equals(TweetType.ALL)) {
			tweets = tweetDao.findTweetsByZip(zip, action.getPage(), action.getPageSize());
		} else {
			tweets = tweetDao.findTweetsByTypeAndZip(type, zip, action.getPage(), action.getPageSize());
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
