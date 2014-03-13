package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class LikeTweetAction implements Action<LikeResult> {
	private Long tweetId;

	public LikeTweetAction() {
	}

	public LikeTweetAction(Long tweetId) {
		this.setTweetId(tweetId);
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
}
