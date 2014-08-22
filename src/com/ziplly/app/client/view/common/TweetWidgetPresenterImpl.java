package com.ziplly.app.client.view.common;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.common.action.CancelTransactionHandler;
import com.ziplly.app.client.view.common.action.CheckCouponPurchaseEligibilityHandler;
import com.ziplly.app.client.view.common.action.DeleteTweetHandler;
import com.ziplly.app.client.view.common.action.PostCommentHandler;
import com.ziplly.app.client.view.common.action.ReportSpamHandler;
import com.ziplly.app.client.view.common.action.TweetLikeHandler;
import com.ziplly.app.client.view.common.action.UpdateCommentHandler;
import com.ziplly.app.client.view.common.action.UpdateTweetHandler;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.CancelCouponPurchaseAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateTweetAction;

public class TweetWidgetPresenterImpl extends BasePresenter implements TweetWidgetPresenter {
  private TweetLikeHandler tweetLikeHandler;
  private PostCommentHandler postCommentHandler;
  private UpdateTweetHandler updateTweetHandler;
  private DeleteTweetHandler deleteTweetHandler;
  private UpdateCommentHandler updateCommentHandler;
  private ReportSpamHandler reportTweetAsSpamHandler;
  private CheckCouponPurchaseEligibilityHandler checkCouponPurchaseEligibilityHandler;
  private CancelTransactionHandler cancelTransactionHandler;
  private ApplicationContext ctx;
  private MessagePresenter messagePresenter;
  
  @Inject
  public TweetWidgetPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus, 
      StringDefinitions stringDefinitions,
      ApplicationContext ctx,
      MessagePresenter messagePresenter) {
   super(dispatcher, eventBus, stringDefinitions, ctx);
   this.messagePresenter = messagePresenter;
  }

  @Override
  public void postComment(TweetWidget widget, CommentDTO comment) {
    if (postCommentHandler == null) {
      postCommentHandler = new PostCommentHandler(eventBus, widget, stringDefinitions);
    }
    
    dispatcher.execute(new CommentAction(comment), postCommentHandler);
  }

  @Override
  public void likeTweet(TweetWidget widget, Long tweetId) {
    if (tweetLikeHandler == null) {
      this.tweetLikeHandler = new TweetLikeHandler(eventBus, widget, stringDefinitions);
    }
    
    LikeTweetAction action = new LikeTweetAction();
    action.setTweetId(tweetId);
    dispatcher.execute(action, tweetLikeHandler);
  }

  @Override
  public void updateTweet(TweetWidget widget, TweetDTO tweet) {
    if (updateTweetHandler == null) {
      this.updateTweetHandler = new UpdateTweetHandler(eventBus, widget, stringDefinitions);
    }
    
    dispatcher.execute(new UpdateTweetAction(tweet), updateTweetHandler);
  }

  @Override
  public void deleteTweet(TweetWidget widget, TweetDTO tweet) {
    if (deleteTweetHandler == null) {
      this.deleteTweetHandler = new DeleteTweetHandler(eventBus, widget, stringDefinitions); 
    }
    
    dispatcher.execute(new DeleteTweetAction(tweet.getTweetId()), deleteTweetHandler);
  }

  @Override
  public void displayPublicProfile(Long accountId) {
    goTo(new AccountPlace(accountId));
  }

  @Override
  public void displayMessage(TweetWidget widget, String message, AlertType alertType) {
    widget.displayMessage(message, alertType);
  }

  @Override
  public void reportTweetAsSpam(TweetWidget widget, TweetDTO tweet, AccountDTO reporter) {
    if (reportTweetAsSpamHandler == null) {
      this.reportTweetAsSpamHandler = new ReportSpamHandler(eventBus, widget, stringDefinitions);
    }
    
    SpamDTO spam = new SpamDTO();
    spam.setTweet(tweet);
    spam.setReporter(reporter);
    dispatcher.execute(new ReportSpamAction(spam), reportTweetAsSpamHandler);
  }

  @Override
  public void updateComment(TweetWidget widget, CommentDTO comment) {
    if (updateCommentHandler == null) {
      this.updateCommentHandler = new UpdateCommentHandler(eventBus, widget, stringDefinitions);
    }
    
    dispatcher.execute(new UpdateCommentAction(comment), updateCommentHandler);
  }

  @Override
  public void checkCouponPurchaseEligibility(TweetWidget widget, CouponDTO coupon) {
    if (checkCouponPurchaseEligibilityHandler == null) {
      this.checkCouponPurchaseEligibilityHandler = new CheckCouponPurchaseEligibilityHandler(eventBus, widget, stringDefinitions);
    }
    
    CheckBuyerEligibilityForCouponAction eligibilityAction = new CheckBuyerEligibilityForCouponAction();
    eligibilityAction.setCoupon(coupon);
    dispatcher.execute(eligibilityAction, checkCouponPurchaseEligibilityHandler);
  }

  @Override
  public void cancelTransaction(TweetWidget widget, long purchaseCouponId) {
    if (cancelTransactionHandler == null) {
      this.cancelTransactionHandler = new CancelTransactionHandler(eventBus, widget, stringDefinitions);
    }
    
    CancelCouponPurchaseAction action = new CancelCouponPurchaseAction();
    action.setPurchaseCouponId(purchaseCouponId);
    dispatcher.execute(action, cancelTransactionHandler);
  }

  @Override
  public void goTo(Place place) {
    eventBus.fireEvent(new PlaceChangeEvent(place));
  }

  @Override
  public AccountDTO getLoggedInAccount() {
    return ctx.getAccount();
  }
  
  public MessagePresenter getMessagePresenter() {
    return messagePresenter;
  }
}
