package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Element;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetLatLngResult;

public interface IAccountView<T extends AccountDTO> extends View<AccountPresenter<T>> {

	void displayProfile(T account);

	void displayPublicProfile(T account);
	
	void clearTweet();
	
	void displayTweets(List<TweetDTO> tweets);

	void displayMessage(String msg, AlertType type);

	Element getTweetSectionElement();

	void addTweets(List<TweetDTO> tweets);

	void displayLocationInMap(GetLatLngResult input);

	void updateAccountDetails(ApplicationContext ctx);

	void updateComment(CommentDTO comment);

	void updateTweetLike(LoveDTO like);

	void updateTweet(TweetDTO tweet);
	
	void openMessageWidget();
	
	void closeMessageWidget();

	void removeTweet(TweetDTO tweet);
}
