package com.ziplly.app.shared;

import com.ziplly.app.model.CommentDTO;

import net.customware.gwt.dispatch.shared.Result;

public class UpdateCommentResult implements Result {
	private CommentDTO comment;

	public UpdateCommentResult() {
	}
	
	public UpdateCommentResult(CommentDTO comment) {
		this.setComment(comment);
	}

	public CommentDTO getComment() {
		return comment;
	}

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}
}
