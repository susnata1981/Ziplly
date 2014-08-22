package com.ziplly.app.client.view.common;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;

/**
 * Presenter used by tweet widget interface.
 */
public interface TweetWidgetPresenter {

  void postComment(TweetWidget widget, CommentDTO comment);

  void likeTweet(TweetWidget widget, Long tweetId);

  void updateTweet(TweetWidget widget, TweetDTO tweet);

  void deleteTweet(TweetWidget widget, TweetDTO tweet);

  void displayPublicProfile(Long accountId);

  void displayMessage(TweetWidget widget, String errorMessage, AlertType error);

  void reportTweetAsSpam(TweetWidget widget, TweetDTO tweet, AccountDTO reporter);

  void updateComment(TweetWidget widget, CommentDTO comment);

  void checkCouponPurchaseEligibility(TweetWidget widget, CouponDTO coupon);

  void cancelTransaction(TweetWidget widget, long transactionId);

//  void sendMessage(TweetWidget widget, ConversationDTO conversation);

  AccountDTO getLoggedInAccount();
  
  void goTo(Place place);

  MessagePresenter getMessagePresenter();
}
