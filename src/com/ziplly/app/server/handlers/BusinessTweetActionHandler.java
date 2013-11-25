package com.ziplly.app.server.handlers;

import java.text.ParseException;
import java.util.Date;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AccountBLIImpl;
import com.ziplly.app.shared.BusinessTweetAction;
import com.ziplly.app.shared.BusinessTweetResult;


public class BusinessTweetActionHandler extends AbstractTweetActionHandler<BusinessTweetAction, BusinessTweetResult> {

	@Inject
	public BusinessTweetActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, TweetDAO tweetDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public BusinessTweetResult execute(BusinessTweetAction action,
			ExecutionContext arg1) throws DispatchException {
		
		if (action == null || action.getTweet() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		Account account = session.getAccount();
		
		if (!(account instanceof BusinessAccount)) {
			throw new AccessError("Illegal Access");
		}
		
		BusinessAccount baccount = (BusinessAccount)account;
		Transaction transaction = baccount.getTransaction();
		
		if (transaction == null) {
			// haven't paid yet, check quota
			try {
				long count = tweetDao.findTweetsByAccountIdAndMonth(session.getAccount().getAccountId(), new Date());
				if (count >= AccountBLIImpl.FREE_TWEET_PER_MONTH_THRESHOLD) {
					throw new NeedsSubscriptionException("You've filled up your quote for the month. Please subscribe to send more tweets.");
				}
			} catch (ParseException e) {
				// should never reach here
				throw new InternalError("Internal error");
			}
		}
		
		Tweet tweet = new Tweet(action.getTweet());
		tweet.setSender(session.getAccount());
		tweetDao.save(tweet);
		
		return new BusinessTweetResult();
	}

	@Override
	public Class<BusinessTweetAction> getActionType() {
		return BusinessTweetAction.class;
	}
}
