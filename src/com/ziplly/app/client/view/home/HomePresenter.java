package com.ziplly.app.client.view.home;

import java.util.List;

import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface HomePresenter extends Presenter {
  
  void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood);

  void displayHashtag(String text);

  void displayTweets(List<TweetDTO> tweets);

  void gotoBusinessPlace();

  void gotoResidentPlace();

  void sendFeedback(String content);

  void getAccountDetails();

  void stop();

  void displayCommunityWall(TweetType type);
}