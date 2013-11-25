package com.ziplly.app.client.activities;

import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.TweetDTO;

public interface TweetPresenter extends Presenter {
	void postComment(CommentDTO comment);
	void likeTweet(Long tweetId);
	void updateTweet(TweetDTO tweet);
	void deleteTweet(TweetDTO tweet);
	void displayPublicProfile(Long accountId);
	void sendTweet(TweetDTO tweet);
}
