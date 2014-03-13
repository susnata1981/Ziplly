package com.ziplly.app.client.widget;

import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface ITweetWidgetView<T extends TweetPresenter> extends View<T> {

	void setWidth(String width);

	void displayTweet(TweetDTO tweet);

	void addComment(CommentDTO comment);

	void updateTweet(TweetDTO tweet);

	void deleteTweet();

	void updateComment(CommentDTO comment);

	void updateLike(LoveDTO like);

	void remove();
}
