package com.ziplly.app.client.activities;

import java.util.List;

import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface HomePresenter extends Presenter {
	void displayTweets(List<TweetDTO> tweets);
	void displayProfile(Long accountId);
	void displayTweetsForCategory(TweetType type);
	void postComment(CommentDTO comment);
	void likeTweet(Long tweetId);
	void updateTweet(TweetDTO tweet);
	void deleteTweet(TweetDTO tweet);
}
