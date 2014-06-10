package com.ziplly.app.client.view.coupon;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.places.TweetDetailsPlace;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.PurchasedCouponAction.ResultStatus;

public class TweetPresenterAdapter implements TweetPresenter {
  private PlaceController placeController;

  public TweetPresenterAdapter(PlaceController placeController) {
    this.placeController = placeController;
  }

  @Override
  public void sendMessage(ConversationDTO conversation) {
  }

  @Override
  public void go(AcceptsOneWidget container) {
  }

  @Override
  public void bind() {
  }

  @Override
  public void goTo(Place place) {
  }

  @Override
  public void postComment(CommentDTO comment) {
  }

  @Override
  public void likeTweet(Long tweetId) {
  }

  @Override
  public void updateTweet(TweetDTO tweet) {
  }

  @Override
  public void deleteTweet(TweetDTO tweet) {
  }

  @Override
  public void displayPublicProfile(Long accountId) {
  }

  @Override
  public void sendTweet(TweetDTO tweet) {
  }

  @Override
  public void displayMessage(String errorMessage, AlertType error) {
  }

  @Override
  public void reportTweetAsSpam(TweetDTO tweet) {
  }

  @Override
  public void deleteImage(String url) {
  }

  @Override
  public void updateComment(CommentDTO comment) {
  }

  @Override
  public void purchaseCoupon(String transactionId, ResultStatus resultStatus, CouponDTO coupon) {
    doLogin();
  }

  @Override
  public void checkCouponPurchaseEligibility(CouponDTO coupon, TweetWidget tweetWidget) {
    TweetDetailsPlace place = new TweetDetailsPlace();
    place.setTweetId(tweetWidget.getTweet().getTweetId());
//    HomePlace place = new HomePlace();
//    place.setTweetId(tweetWidget.getTweet().getTweetId());
    placeController.goTo(place);
  }

  @Override
  public void getCouponFormActionUrl(CouponFormWidget couponFormWidget) {
  }

  @Override
  public void initializeUploadForm(FormUploadWidget formUploadWidget) {
  }
  
  private void doLogin() {
    placeController.goTo(new LoginPlace());
  }

  @Override
  public void cancelTransaction(long transactionId) {
    // TODO Auto-generated method stub
    
  }
}
