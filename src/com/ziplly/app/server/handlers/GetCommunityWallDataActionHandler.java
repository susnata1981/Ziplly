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
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class GetCommunityWallDataActionHandler extends AbstractTweetActionHandler<GetCommunityWallDataAction, GetCommunityWallDataResult>{

	@Inject
	public GetCommunityWallDataActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, TweetDAO tweetDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public GetCommunityWallDataResult execute(GetCommunityWallDataAction action,
			ExecutionContext arg1) throws DispatchException {
		
		validateSession();
		
		int zip = session.getAccount().getZip();
		List<Tweet> tweets = tweetDao.findTweetsByZip(new Integer(zip));
		List<TweetDTO> tweetDtos = Lists.newArrayList();
		for(Tweet t : tweets) {
			TweetDTO tdto = new TweetDTO(t);
			tweetDtos.add(tdto);
		}
		
		GetCommunityWallDataResult gcwdr = new GetCommunityWallDataResult();
		gcwdr.setTweets(tweetDtos);
		return gcwdr;
	}

	@Override
	public Class<GetCommunityWallDataAction> getActionType() {
		return GetCommunityWallDataAction.class;
	}
	

}
