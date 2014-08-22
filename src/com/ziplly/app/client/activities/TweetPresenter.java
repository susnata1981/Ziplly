package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.ziplly.app.client.view.coupon.CouponFormWidgetModal;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;

public interface TweetPresenter extends Presenter {
	
	void postComment(CommentDTO comment);

	void likeTweet(Long tweetId);

	void updateTweet(TweetDTO tweet);

	void deleteTweet(TweetDTO tweet);

	void displayPublicProfile(Long accountId);

	void sendTweet(TweetDTO tweet);

	void displayMessage(String errorMessage, AlertType error);

	void reportTweetAsSpam(TweetDTO tweet);

	void deleteImage(String url);

	void updateComment(CommentDTO comment);

//	void purchaseCoupon(String transactionId, PurchasedCouponAction.ResultStatus resultStatus, CouponDTO coupon);

	void checkCouponPurchaseEligibility(CouponDTO coupon, TweetWidget tweetWidget);

	void getCouponFormActionUrl(CouponFormWidgetModal couponFormWidget);

	void initializeUploadForm(FormUploadWidget formUploadWidget);

  void cancelTransaction(long transactionId);

  void sendMessage(ConversationDTO conversation);
//	TweetWidget getTweetWidget();

  void goTo(Place place);
}
