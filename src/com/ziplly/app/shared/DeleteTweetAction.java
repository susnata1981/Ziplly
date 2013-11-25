package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class DeleteTweetAction implements Action<DeleteTweetResult> {
	private Long tweetId;

	public DeleteTweetAction() {
	}
	
	public DeleteTweetAction(Long tweetId) {
		this.setTweetId(tweetId);
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
}
