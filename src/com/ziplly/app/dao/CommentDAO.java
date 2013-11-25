package com.ziplly.app.dao;

import com.ziplly.app.model.Comment;

public interface CommentDAO {
	void save(Comment comment);
	void delete(Comment comment);
	Long findCommentCountByAccountId(Long accountId);
}
