package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Element;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.TweetView.TweetWidgetRenderingStatus;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface ITweetView<T extends Presenter> extends View<T> {
	void add(List<TweetDTO> tweets);

	void addComment(CommentDTO comment);

	void updateComment(CommentDTO comment);

	void updateLike(LoveDTO like);

	void updateTweet(TweetDTO tweet);

	void remove(TweetDTO tweet);

	void setWidth(String width);

	void addTweet(TweetDTO tweet);

	Element getTweetSection();

	void insertTweet(TweetDTO tweet);

	void displayMessage(String msg, AlertType type);

	void setHeight(String tweetWidgetHeight);

	void displayTweets(List<TweetDTO> tweets, TweetViewDisplayStatusCallback callback);

	void displayTweets(List<TweetDTO> tweets);

	void addTweet(TweetDTO tweet, TweetWidgetRenderingStatus callback);

	void displayNoTweetsMessage();

	void refreshTweet(TweetDTO tweet);
}
