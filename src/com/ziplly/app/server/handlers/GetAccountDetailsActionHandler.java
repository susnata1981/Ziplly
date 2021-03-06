package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.ConversationDAO;
import com.ziplly.app.dao.LikeDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class GetAccountDetailsActionHandler extends AbstractAccountActionHandler<GetAccountDetailsAction, GetAccountDetailsResult>{
	private ConversationDAO conversationDao;
	private TweetDAO tweetDao;
	private CommentDAO commentDao;
	private LikeDAO likeDao;

	@Inject
	public GetAccountDetailsActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, 
			ConversationDAO conversationDao, 
			TweetDAO tweetDao,
			CommentDAO commentDao,
			LikeDAO likeDao) {
		super(accountDao, sessionDao, accountBli);
		this.conversationDao = conversationDao;
		this.tweetDao = tweetDao;
		this.commentDao = commentDao;
		this.likeDao = likeDao;
	}

	@Override
	public GetAccountDetailsResult execute(GetAccountDetailsAction action,
			ExecutionContext arg1) throws DispatchException {
		
		validateSession();
		
		Long accountId = session.getAccount().getAccountId();

		Long unreadMessageCount = conversationDao.getUnreadConversationForAccount(accountId);
		Long totalTweets = tweetDao.findTweetsCountByAccountId(accountId);
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
	public Class<GetAccountDetailsAction> getActionType() {
		return GetAccountDetailsAction.class;
	}

}
