package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CommentDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Comment;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;

public class CommentActionHandler extends
    AbstractAccountActionHandler<CommentAction, CommentResult> {
	private CommentDAO commentDao;

	@Inject
	public CommentActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    CommentDAO commentDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.commentDao = commentDao;
	}

	@Override
	public CommentResult
	    doExecute(CommentAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getComment() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		Comment comment = new Comment(action.getComment());
		comment.setAuthor(session.getAccount());
		commentDao.save(comment);

		return new CommentResult(EntityUtil.clone(comment));
	}

	@Override
	public Class<CommentAction> getActionType() {
		return CommentAction.class;
	}

}
