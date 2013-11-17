package com.ziplly.app.client.view;

import java.util.List;

import com.ziplly.app.client.activities.HomePresenter2;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface IHomeView2 extends View<HomePresenter2>{
	void display(List<TweetDTO> tweets);
	void displayCommentSuccessfull();
	void displayCommentFailure();
	void displayLikeSuccessful();
	void displayLikeUnsuccessful();
	void updateTweet(TweetDTO tweet);
	void displayInvalidAccessMessage();
	void displayInternalError();
	void updateComment(CommentDTO comment);
	void displayTweetUpdated();
	void updateTweetLike(LoveDTO like);
}
