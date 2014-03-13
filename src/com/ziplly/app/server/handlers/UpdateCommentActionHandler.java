package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Comment;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateCommentResult;

public class UpdateCommentActionHandler extends
    AbstractAccountActionHandler<UpdateCommentAction, UpdateCommentResult> {
	private CommentDAO commentDao;

	@Inject
	public UpdateCommentActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    CommentDAO commentDao) {
		super(accountDao, sessionDao, accountBli);
		this.commentDao = commentDao;
	}

	@Override
	public UpdateCommentResult
	    execute(UpdateCommentAction action, ExecutionContext arg1) throws DispatchException {

		Preconditions.checkArgument(action != null && action.getComment() != null);
		validateSession();

		CommentDTO commentDto = action.getComment();
		if (!commentDto.getAuthor().equals(session.getAccount())) {
			throw new AccessError();
		}

		Comment comment = new Comment(commentDto);
		commentDao.update(comment);
		return new UpdateCommentResult(commentDto);
	}

	@Override
	public Class<UpdateCommentAction> getActionType() {
		return UpdateCommentAction.class;
	}

}
