package com.ziplly.app.client.view.common;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Element;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public interface ITweetListView {
  
  void insertLast(List<TweetDTO> tweets, RenderingStatusCallback callback);

  void addComment(CommentDTO comment);

  void updateComment(CommentDTO comment);

  void updateLike(LoveDTO like);

  void updateTweet(TweetDTO tweet);

  void remove(TweetDTO tweet);

  void setWidth(String width);

//  void addTweet(TweetDTO tweet);
//  void addTweet(TweetDTO tweet, TweetWidgetRenderingStatus callback);
//  void displayTweets(List<TweetDTO> tweets, TweetViewDisplayStatusCallback callback);
//  void displayTweets(List<TweetDTO> tweets);

  Element getTweetSection();

  void insertFirst(TweetDTO tweet);

  void displayMessage(String msg, AlertType type);

  void setHeight(String tweetWidgetHeight);

  void displayNoTweetsMessage();

  void refreshTweet(TweetDTO tweet);

  void clear();

  void resetTweets();
}
