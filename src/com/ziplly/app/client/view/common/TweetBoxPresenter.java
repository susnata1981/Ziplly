package com.ziplly.app.client.view.common;

import com.ziplly.app.model.TweetDTO;

public interface TweetBoxPresenter {
  
  void sendTweet(TweetDTO tweet);
  
  void deleteImage(String url);
}
