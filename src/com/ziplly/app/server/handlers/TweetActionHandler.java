package com.ziplly.app.server.handlers;

import java.text.ParseException;
import java.util.Date;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.AccountBLIImpl;
import com.ziplly.app.server.TweetNotificationBLI;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;

public class TweetActionHandler extends AbstractTweetActionHandler<TweetAction, TweetResult> {

	private TweetNotificationBLI tweetNotificationBli;

	@Inject
	public TweetActionHandler(AccountDAO accountDao, 
			SessionDAO sessionDao,
			TweetDAO tweetDao,
			AccountBLI accountBli,
			TweetNotificationBLI tweetNotificationBli) {
		super(accountDao, sessionDao, tweetDao, accountBli);
		this.tweetNotificationBli = tweetNotificationBli;
	}

	@Override
	public TweetResult execute(TweetAction action, ExecutionContext arg1)
			throws DispatchException {
		if (action == null || action.getTweet() == null) {
			throw new IllegalArgumentException();
		}
		validateSession();
		
		Account account = session.getAccount();
		
		// check usage limits for business tweets
		if (account instanceof BusinessAccount) {
			checkUsage();
		}
		
		Tweet tweet = new Tweet(action.getTweet());
		tweet.setSender(session.getAccount());
		TweetDTO saveTweet = tweetDao.save(tweet);
		
		if (tweetNotificationBli.shouldNotification(saveTweet)) {
			accountBli.sendEmailByZip(tweet.getSender(), tweet.getType().getNotificationType(), EmailTemplate.SECURITY_ALERT);
		}
		
		TweetResult result = new TweetResult();
		result.setTweet(saveTweet);
		return result;
	}

	private void checkUsage() throws NeedsSubscriptionException, InternalError, UsageLimitExceededException {
		boolean enablePaymentPlan = Boolean.valueOf(System.getProperty(StringConstants.ENABLE_PAYMENT_PLAN, "false"));
		
		// Wire on Wire off
		if (!enablePaymentPlan) {
			return;
		}
		
		BusinessAccount baccount = (BusinessAccount)session.getAccount();
		long count = 0;
		
		try {
			count = tweetDao.findTweetsByAccountIdAndMonth(session.getAccount().getAccountId(), new Date());
		} catch (ParseException e) {
			// should never reach here
			throw new InternalError("Internal error");
		}
		
		SubscriptionPlan plan = findActiveSubscriptionPlan(baccount);
		if (plan == null) {
			// haven't paid yet, check quota
			if (count >= AccountBLIImpl.FREE_TWEET_PER_MONTH_THRESHOLD) {
				throw new NeedsSubscriptionException(StringConstants.NEEDS_SUBSCRIPTION_EXCEPTION);
			}
		} else {
			if (count >= (plan.getTweetsAllowed() + AccountBLIImpl.FREE_TWEET_PER_MONTH_THRESHOLD)) {
				throw new UsageLimitExceededException(StringConstants.USAGE_LIMIT_EXCEEDED_EXCEPTION);
			}
		}
	}

	private SubscriptionPlan findActiveSubscriptionPlan(BusinessAccount account) {
		for(Transaction txn : account.getTransactions()) {
			if (txn.getStatus() == TransactionStatus.ACTIVE) {
				return txn.getPlan();
			}
		}
		return null;
	}

	@Override
	public Class<TweetAction> getActionType() {
		return TweetAction.class;
	}
}
