package com.ziplly.app.client.widget;

import com.ziplly.app.client.activities.HomePresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface ITweetWidgetView extends View<HomePresenter> {

	void displayTweet(TweetDTO tweet);

	void addComment(CommentDTO comment);

	void updateTweet();

	void deleteTweet();

	void updateComment(CommentDTO comment);

	void updateLike(LoveDTO like);
}
