package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.NotSharedError;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;

public class GetTweetForUserActionHandler extends
		AbstractAccountActionHandler<GetTweetForUserAction, GetTweetForUserResult> {
	private TweetDAO tweetDao;

	@Inject
	public GetTweetForUserActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, TweetDAO tweetDao) {
		super(accountDao, sessionDao, accountBli);
		this.tweetDao = tweetDao;
	}

	@Override
	public GetTweetForUserResult execute(GetTweetForUserAction action, ExecutionContext arg1)
			throws DispatchException {

		// apply privacy settings
		try {
			applyPrivacySettings(action.getAccountId());
		} catch (NotSharedError ex) {
			throw ex;
		}

		try {
			List<TweetDTO> tweets = tweetDao.findTweetsByAccountId(action.getAccountId(),
					action.getPage(), action.getPageSize());
			GetTweetForUserResult result = new GetTweetForUserResult(tweets);
			return result;
		} catch (NotFoundException nfe) {
			throw new NotFoundException();
		}
	}

	private void applyPrivacySettings(long accountId) throws NotFoundException, NotSharedError {
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

		AccountDTO account = accountDao.findById(accountId);
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			if (ps.getSection() == AccountDetailsType.TWEETS) {
				if (!userLoggedIn) {
					if (ps.getSetting() != ShareSetting.PUBLIC) {
						throw new NotSharedError(StringConstants.TWEET_NOT_SHARED);
					}
				} else {
					Account requestingAccount = session.getAccount();
					if (!account.getNeighborhood().getParentNeighborhood().equals(requestingAccount.getNeighborhood().getParentNeighborhood())) {
						throw new NotSharedError(StringConstants.TWEET_NOT_SHARED);
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
