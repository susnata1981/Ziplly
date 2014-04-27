package com.ziplly.app.server.handlers;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.NotSharedException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Location;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;

public class GetTweetForUserActionHandler extends
    AbstractAccountActionHandler<GetTweetForUserAction, GetTweetForUserResult> {
	private TweetDAO tweetDao;

	@Inject
	public GetTweetForUserActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    TweetDAO tweetDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}

	@Override
	public GetTweetForUserResult
	    doExecute(GetTweetForUserAction action, ExecutionContext arg1) throws DispatchException {

		// apply privacy settings
		try {
			applyPrivacySettings(action.getAccountId());
		} catch (NotSharedException ex) {
			throw ex;
		}

		try {
			List<TweetDTO> tweets =
			    tweetDao.findTweetsByAccountId(
			        action.getAccountId(),
			        action.getPage(),
			        action.getPageSize());
			GetTweetForUserResult result = new GetTweetForUserResult(tweets);
			return result;
		} catch (NotFoundException nfe) {
			throw new NotFoundException();
		}
	}

	private void applyPrivacySettings(long accountId) throws NotFoundException, NotSharedException {
		boolean userLoggedIn = false;
		try {
			validateSession();
			if (session.getAccount() != null && session.getAccount().getAccountId() == accountId) {
				return;
			}
			userLoggedIn = true;
		} catch (NeedsLoginException ex) {
			userLoggedIn = false;
		}

		// ONLY APPLICABLE FOR PERSONAL ACCOUNTS.
		AccountDTO account = accountDao.findById(accountId);
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			if (ps.getSection() == AccountDetailsType.TWEETS) {
				if (!userLoggedIn) {
					if (ps.getSetting() != ShareSetting.PUBLIC) {
						throw new NotSharedException(StringConstants.TWEET_NOT_SHARED);
					}
				} else {
					Account requestingAccount = session.getAccount();
					Iterator<Location> locationIterator = requestingAccount.getLocations().iterator();
					if (!locationIterator.hasNext()) {
						throw new NotSharedException(StringConstants.TWEET_NOT_SHARED);
					}

					Location location = locationIterator.next();
					if (session.getLocation().getNeighborhood().getParentNeighborhood().getNeighborhoodId() != location
					    .getNeighborhood()
					    .getParentNeighborhood()
					    .getNeighborhoodId()) {
						throw new NotSharedException(StringConstants.TWEET_NOT_SHARED);
					}
				}
			}
		}
	}

	@Override
	public Class<GetTweetForUserAction> getActionType() {
		return GetTweetForUserAction.class;
	}
}
