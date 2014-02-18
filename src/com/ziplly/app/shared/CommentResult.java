package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.CommentDTO;

public class CommentResult implements Result {
	private CommentDTO comment;

	public CommentResult() {
	}

	public CommentResult(CommentDTO comment) {
		this.setComment(comment);
	}

	public CommentDTO getComment() {
		return comment;
	}

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}
}
