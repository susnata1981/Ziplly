package com.ziplly.app.client.view.home;

import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.common.RenderingStatusCallback;
import com.ziplly.app.client.view.common.TweetListView;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.GetNeighborhoodDetailsResult;

public interface HomeView extends View<HomePresenter> {
  
  void addComment(CommentDTO comment);

//  void addTweet(TweetDTO tweet);

  void insertLast(List<TweetDTO> tweets, RenderingStatusCallback callback);

  void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler);

  void display(List<TweetDTO> tweets);

  void displayCommunitySummaryDetails(GetNeighborhoodDetailsResult result);

  void displayHashtag(List<HashtagDTO> hashtags);

  void displayMap(String address);

  void displayMessage(String message, AlertType error);

  void displayNeighborhoodImage(NeighborhoodDTO neighborhood);

  void displayNewMembers(List<AccountDTO> accounts);

  void displayProfileImagePreview(String imageUrl);

  void displaySummaryData(NeighborhoodDTO neighborhood);

  void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList);

  Element getTweetSectionElement();

  TweetListView getTweetView();

  TweetBox getTweetWidget();

  void highlightTweetType(TweetType type);

  void insertFirst(TweetDTO tweet);

  void removeTweet(TweetDTO tweet);

  void resetImageUploadUrl();

  void resizeMap();

  void setImageUploadUrl(String imageUrl);

  void setUnreadMessageCount(long count);

  void updateComment(CommentDTO comment);

  void updateTweet(TweetDTO tweet);

  void updateTweetCategoryCount(Map<TweetType, Integer> tweetCounts);

  void updateTweetLike(LoveDTO like);

  void updateTweets(List<TweetDTO> tweets);

  void refreshTweet(TweetDTO tweet);

  FormPanel getUploadForm();

  void resetTweets();
}