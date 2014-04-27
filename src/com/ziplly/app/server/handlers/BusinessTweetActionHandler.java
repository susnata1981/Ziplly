package com.ziplly.app.server.handlers;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.AccountBLIImpl;
import com.ziplly.app.shared.BusinessTweetAction;
import com.ziplly.app.shared.BusinessTweetResult;

@Deprecated
public class BusinessTweetActionHandler extends
    AbstractTweetActionHandler<BusinessTweetAction, BusinessTweetResult> {

	@Inject
	public BusinessTweetActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TweetDAO tweetDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
	}

	@Override
	public BusinessTweetResult
	    doExecute(BusinessTweetAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getTweet() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		Account account = session.getAccount();

		if (!(account instanceof BusinessAccount)) {
			throw new AccessException("Illegal Access");
		}

		BusinessAccount baccount = (BusinessAccount) account;
		Set<Transaction> transaction = baccount.getTransactions();

		if (transaction == null) {
			// haven't paid yet, check quota
			try {
				long count =
				    tweetDao.findTweetsByAccountIdAndMonth(session.getAccount().getAccountId(), new Date());
				if (count >= AccountBLIImpl.FREE_TWEET_PER_MONTH_THRESHOLD) {
					throw new NeedsSubscriptionException(
					    "You've filled up your quote for the month. Please subscribe to send more tweets.");
				}
			} catch (ParseException e) {
				// should never reach here
				throw new InternalException("Internal error");
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
