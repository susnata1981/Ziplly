package com.ziplly.app.client.view;

import java.util.List;

import com.google.gwt.user.client.Element;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface ITweetView<T extends Presenter> extends View<T> {
	void displayTweets(List<TweetDTO> tweets);
	void add(List<TweetDTO> tweets);
	void updateComment(CommentDTO comment);
	void updateLike(LoveDTO like);
	void updateTweet(TweetDTO tweet);
	void remove(TweetDTO tweet);
	void setWidth(String width);
	void addTweet(TweetDTO tweet);
	Element getTweetSection();
}
