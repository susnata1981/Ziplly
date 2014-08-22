package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class DeleteTweetAction implements Action<DeleteTweetResult> {
	private long tweetId;

	public DeleteTweetAction() {
	}

	public DeleteTweetAction(long tweetId) {
		this.setTweetId(tweetId);
	}

	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
}
