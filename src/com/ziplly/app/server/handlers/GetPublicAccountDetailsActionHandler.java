package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;

public class GetPublicAccountDetailsActionHandler extends
    AbstractAccountActionHandler<GetPublicAccountDetailsAction, GetAccountDetailsResult> {

	private ConversationDAO conversationDao;
	private TweetDAO tweetDao;
	private CommentDAO commentDao;
	private LikeDAO likeDao;

	@Inject
	public GetPublicAccountDetailsActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    ConversationDAO conversationDao,
	    TweetDAO tweetDao,
	    CommentDAO commentDao,
	    LikeDAO likeDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
		this.tweetDao = tweetDao;
		this.commentDao = commentDao;
		this.likeDao = likeDao;
	}

	@Override
	public GetAccountDetailsResult
	    doExecute(GetPublicAccountDetailsAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getAccountId() == null) {
			throw new IllegalArgumentException();
		}

		Long accountId = action.getAccountId();
		Long unreadMessageCount = conversationDao.getUnreadConversationCountForAccount(accountId);
		Long totalTweets = tweetDao.findTweetCountByAccountId(accountId);
		Long totalComments = commentDao.findCommentCountByAccountId(accountId);
		Long totalLikes = likeDao.findLikeCountByAccoutId(accountId);

		GetAccountDetailsResult result = new GetAccountDetailsResult();
		result.setUnreadMessages(unreadMessageCount.intValue());
		result.setTotalTweets(totalTweets.intValue());
		result.setTotalComments(totalComments.intValue());
		result.setTotalLikes(totalLikes.intValue());
		return result;
	}

	@Override
	public Class<GetPublicAccountDetailsAction> getActionType() {
		return GetPublicAccountDetailsAction.class;
	}
}
