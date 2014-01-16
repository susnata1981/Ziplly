package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.TweetDTO;

public interface TweetPresenter extends SendMessagePresenter {
	void postComment(CommentDTO comment);
	void likeTweet(Long tweetId);
	void updateTweet(TweetDTO tweet);
	void deleteTweet(TweetDTO tweet);
	void displayPublicProfile(Long accountId);
	void sendTweet(TweetDTO tweet);
	TweetWidget getTweetWidget();
	void displayMessage(String errorMessage, AlertType error);
	void reportTweetAsSpam(TweetDTO tweet);
	void deleteImage(String url);
}
