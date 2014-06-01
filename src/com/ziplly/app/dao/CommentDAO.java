package com.ziplly.app.dao;

import com.ziplly.app.server.model.jpa.Comment;

public interface CommentDAO {
	void save(Comment comment);

	void delete(Comment comment);

	Long findCommentCountByAccountId(Long accountId);

	void update(Comment comment);
}
