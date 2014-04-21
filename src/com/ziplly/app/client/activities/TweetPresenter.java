package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.widget.CouponFormWidget;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.overlay.GoogleWalletFailureResult;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;

public interface TweetPresenter extends SendMessagePresenter {
	
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

	void purchaseCoupon(GoogleWalletSuccessResult result, CouponDTO coupon);

	void checkCouponPurchaseEligibility(CouponDTO coupon, TweetWidget tweetWidget);

	void getCouponFormActionUrl(CouponFormWidget couponFormWidget);

	void initializeUploadForm(FormUploadWidget formUploadWidget);
	
//	TweetWidget getTweetWidget();
}
