package com.ziplly.app.server;

import java.util.List;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.PasswordRecoveryDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;
import com.ziplly.app.model.TweetStatus;

@SuppressWarnings("unused")
public class AdminBLIImpl implements AdminBLI {
	private AccountDAO accountDao;
	private SessionDAO sessionDao;
	private InterestDAO interestDao;
	private TransactionDAO transactionDao;
	private SubscriptionPlanDAO subscriptionPlanDao;
	private PasswordRecoveryDAO passwordRecoveryDao;
	private BlobstoreService blobstoreService;
	private EmailService emailService;
	private TweetDAO tweetDao;

	@Inject
	public AdminBLIImpl(AccountDAO accountDao, SessionDAO sessionDao, TweetDAO tweetDao, InterestDAO interestDao,
			TransactionDAO transactionDao, SubscriptionPlanDAO subscriptionPlanDao,
			PasswordRecoveryDAO passwordRecoveryDao, EmailService emailService) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.tweetDao = tweetDao;
		this.interestDao = interestDao;
		this.transactionDao = transactionDao;
		this.subscriptionPlanDao = subscriptionPlanDao;
		this.passwordRecoveryDao = passwordRecoveryDao;
		this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		this.emailService = emailService;
	}

	@Override
	public List<TweetDTO> getTweets(int start, int end, TweetSearchCriteria tsc) {
		if (tsc == null || end <= start) {
			throw new IllegalArgumentException("Invalid arugments to getTweets");
		}

		String query = buildQuery(tsc);
		System.out.println("Query = "+query);
		return tweetDao.findTweets(query, start, end);
	}

	@Override
	public Long getTotalTweetCount(TweetSearchCriteria tsc) {
		if (tsc == null) {
			throw new IllegalArgumentException("Invalid arugments to getTweets");
		}

		String query = buildQuery(tsc);
		String countQuery = "select count(*) " + query;
		return tweetDao.findTotalTweetCount(countQuery);
	}
	
	// Builds the where clause
	private String buildQuery(TweetSearchCriteria tsc) {
		StringBuilder sb = new StringBuilder("from Tweet t ");
		boolean addedWhereClause = false;
		
		if (tsc.getZip() != 0) {
			addedWhereClause = true;
			sb.append("where t.sender.zip = " + tsc.getZip());
		}

		StringBuilder temp = new StringBuilder();
		int totalTypes = tsc.getType().size();
		if (totalTypes > 0) {
			for (int i = 0; i < totalTypes; i++) {
				temp.append(tsc.getType().get(i).ordinal());
				if (i != (totalTypes-1)) {
					temp.append(", ");
				}
			}
			if (!addedWhereClause) {
				sb.append("where");
			} else {
				sb.append("and");
			}
			addedWhereClause = true;
			sb.append(" type in ("+temp+") ");			
		}
		
		TweetStatus status = tsc.getStatus();
		if (status != TweetStatus.ALL) {
			if (!addedWhereClause) {
				sb.append("where");
			} else {
				sb.append("and");
			}
			addedWhereClause = true;
			sb.append(" status = "+status.ordinal());
		}
		
		return sb.toString();
	}
}