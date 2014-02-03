package com.ziplly.app.shared;

import com.ziplly.app.model.CommentDTO;

import net.customware.gwt.dispatch.shared.Action;

public class UpdateCommentAction implements Action<UpdateCommentResult>{
	private CommentDTO comment;

	public UpdateCommentAction() {
	}
	
	public UpdateCommentAction(CommentDTO comment) {
		this.setComment(comment);
	}

	public CommentDTO getComment() {
		return comment;
	}

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}

}
