package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountNotificationDAO;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class GetAccountDetailsActionHandler extends
    AbstractAccountActionHandler<GetAccountDetailsAction, GetAccountDetailsResult> {
	private ConversationDAO conversationDao;
	private TweetDAO tweetDao;
	private CommentDAO commentDao;
	private LikeDAO likeDao;
	private AccountNotificationDAO accountNotificationDao;

	@Inject
	public GetAccountDetailsActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    ConversationDAO conversationDao,
	    TweetDAO tweetDao,
	    CommentDAO commentDao,
	    LikeDAO likeDao,
	    AccountNotificationDAO accountNotificationDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
		this.tweetDao = tweetDao;
		this.commentDao = commentDao;
		this.likeDao = likeDao;
		this.accountNotificationDao = accountNotificationDao;
	}

	@Override
	public GetAccountDetailsResult
	    doExecute(GetAccountDetailsAction action, ExecutionContext arg1) throws DispatchException {

		validateSession();

		Long accountId = session.getAccount().getAccountId();

		Long unreadMessageCount = conversationDao.getUnreadConversationCountForAccount(accountId);
		Long totalTweets = tweetDao.findTweetsCountByAccountId(accountId);
		Long totalComments = commentDao.findCommentCountByAccountId(accountId);
		Long totalLikes = likeDao.findLikeCountByAccoutId(accountId);

		List<AccountNotificationDTO> notifications =
		    accountNotificationDao.findAccountNotificationByAccountId(session
		        .getAccount()
		        .getAccountId());

		GetAccountDetailsResult result = new GetAccountDetailsResult();
		result.setUnreadMessages(unreadMessageCount.intValue());
		result.setTotalTweets(totalTweets.intValue());
		result.setTotalComments(totalComments.intValue());
		result.setTotalLikes(totalLikes.intValue());
		result.setAccountNotifications(notifications);
		return result;
	}

	@Override
	public Class<GetAccountDetailsAction> getActionType() {
		return GetAccountDetailsAction.class;
	}

}
