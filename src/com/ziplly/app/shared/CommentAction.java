package com.ziplly.app.shared;

import com.ziplly.app.model.CommentDTO;

import net.customware.gwt.dispatch.shared.Action;

public class CommentAction implements Action<CommentResult>{
	private CommentDTO comment;
	
	public CommentAction(CommentDTO comment) {
		this.setComment(comment);
	}
	
	public CommentAction() {
	}

	public CommentDTO getComment() {
		return comment;
	}

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}
}
