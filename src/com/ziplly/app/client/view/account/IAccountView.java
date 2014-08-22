package com.ziplly.app.client.view.account;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.view.PendingActionTypes;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.common.RenderingStatusCallback;
import com.ziplly.app.client.view.common.TweetListView;
import com.ziplly.app.client.widget.EmailWidget;
import com.ziplly.app.client.widget.TweetBox;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngResult;

public interface IAccountView<T extends AccountDTO> extends View<AccountViewPresenter<T>> {

	void displayProfile(T account);

	void displayPublicProfile(T account);

	void clearTweet();

	void displayTweets(List<TweetDTO> tweets, boolean displayNoTweetsMessage);

	void displayModalMessage(String msg, AlertType type);

	Element getTweetSectionElement();

	void addTweets(List<TweetDTO> tweets);

	void displayLocationInMap(GetLatLngResult input);

	void updateAccountDetails(AccountDetails ctx);

	void updateComment(CommentDTO comment);

	void updateTweetLike(LoveDTO like);

	void updateTweet(TweetDTO tweet);

	void openMessageWidget();

	void closeMessageWidget();

	void removeTweet(TweetDTO tweet);

	void setImageUploadUrl(String imageUrl);

	void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler);

	void displayProfileImagePreview(String imageUrl);

	void resetImageUploadUrl();

	void addComment(CommentDTO comment);

	void displayTweetViewMessage(String msg, AlertType type);

	void displayTargetNeighborhoods(List<NeighborhoodDTO> targetNeighborhoodList);

	void updatePublicAccountDetails(GetAccountDetailsResult result);

	void displayAccontUpdate(List<PendingActionTypes> updates);
	
	void displayMessage(String msg, AlertType type);

	void displayMap(String address);
	
	TweetListView getTweetView();
	
	TweetBox getTweetWidget();

	EmailWidget getEmailWidget();

	void displayPurchasedCoupons(List<CouponItemDTO> transactions);

	void setCouponTransactionCount(Long totalTransactions);

	void displayQrCode(String url);

	void displayProfileSection(boolean display);

	void displayCouponTransactions();

  void displayTweets(List<TweetDTO> tweets,
      boolean displayNoTweetsMessage,
      RenderingStatusCallback callback);
}