package com.ziplly.app.client.view.account;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.InfiniteScrollHandler;
import com.ziplly.app.client.activities.TweetViewBinder;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.view.common.action.AccountNotificationHandler;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.SendEmailAction;

public abstract class BaseAccountViewPresenter<T extends AccountDTO> implements AccountViewPresenter<T>, InfiniteScrollHandler {
  protected int tweetPageIndex;
  protected int TWEETS_PER_PAGE = 10;
  protected CachingDispatcherAsync dispatcher;
  protected EventBus eventBus;
  protected ApplicationContext ctx;
  protected IAccountView<T> view;

  protected List<TweetDTO> lastTweetList;
  protected TweetViewBinder binder;
  private AccountPlace place;
  private boolean startInfiniteScrollThread;
  private AcceptsOneWidget panel;
  
  public BaseAccountViewPresenter(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      ApplicationContext ctx,
      IAccountView<T> view,
      AcceptsOneWidget panel,
      AccountPlace place) {
    this.dispatcher = dispatcher;
    this.eventBus = eventBus;
    this.ctx = ctx;
    this.view = view;
    this.panel = panel;
    this.place = place;
    bind();
    setupHandlers();
    start();
  }

  private void start() {
    // public account
    if (place.getAccountId() != ctx.getAccount().getAccountId()) {
      displayPublicProfile(place.getAccountId());
    } else {
      System.out.println("Displaying public profile ...");
      displayProfile();
      if (place.isShowTransactions()) {
        view.displayCouponTransactions();
      } else {
        fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, false);
      }
    }
  }

  protected void getAccountDetails() {
    dispatcher.execute(new GetAccountDetailsAction(), new GetAccountDetailsHandler<T>(eventBus, view, ctx));
  }

  @Override
  public void invitePeople(List<String> emails) {
    SendEmailAction action = new SendEmailAction();
    action.setEmailTemplate(EmailTemplate.INVITE_PEOPLE);
    action.setEmailList(emails);
    dispatcher.execute(action, new SendEmailHandler<T>(eventBus, view));
  }

  protected void fetchTweets(
      long accountId,
      int page,
      int pageSize,
      final boolean displayNoTweetsMessage) {

    eventBus.fireEvent(new LoadingEventStart());
    GetTweetForUserAction action = new GetTweetForUserAction(accountId, page, pageSize);
    dispatcher.execute(action, new GetTweetForUserHandler<T>(eventBus, view, this, displayNoTweetsMessage));
  }

  protected void setupHandlers() {
    eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {
      
      @Override
      public void onEvent(AccountUpdateEvent event) {
        ctx.setAccount(event.getAccount());
        displayProfile();
      }
      
    });

//    eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {
//      
//      @Override
//      public void onEvent(AccountDetailsUpdateEvent event) {
//        onAccountDetailsUpdate(event.getAccountDetails());
//      }
//      
//    });
  }

  protected void onAccountDetailsUpdate(GetAccountDetailsResult result) {
//    ctx.setAccountDetails(result);
    view.updateAccountDetails(ctx.getAccountDetails());
  }

  protected void displayMap(NeighborhoodDTO n) {
    view.displayMap(n.getPostalCodes().get(0).toString());
  }

  protected void stopThreads() {
    if (binder != null) {
      binder.stop();
    }
  }

  protected void getPublicAccountDetails(long accountId) {
    dispatcher.execute(
        new GetPublicAccountDetailsAction(accountId),
        new GetPublicAccountDetailsHandler(eventBus, view));
  }

  protected final boolean isScrollThreadStarted() {
    return startInfiniteScrollThread;
  }
  
  protected void startInfiniteScrollThread() {
    // If already started, return
    System.out.println("Starting scrolling thread...");
    if (startInfiniteScrollThread) {
      return;
    }
    
    if (binder != null) {
      binder.stop();
    }
    
    binder = new TweetViewBinder(view.getTweetSectionElement(), this);
    binder.start();
    startInfiniteScrollThread = true;
  }

  @Override
  public boolean hasMoreElements() {
    if (lastTweetList == null) {
      return false;
    }
    return lastTweetList.size() == TWEETS_PER_PAGE;
  }

  @Override
  public void onScrollBottomHit() {
    System.out.println("Scroll bottom hit detected...");
    tweetPageIndex++;
    long accountId = place.getAccountId() != 0 ? place.getAccountId() : ctx.getAccount().getAccountId();
    fetchTweets(accountId, tweetPageIndex, TWEETS_PER_PAGE, false);
  }

  @Override
  public void displayProfile() {
    view.displayProfile((T) ctx.getAccount());

    // Display target neighborhood
    view.displayTargetNeighborhoods(ctx.getTargetNeighborhoodList());

    startInfiniteScrollThread();
    displayMap(ctx.getCurrentNeighborhood());
    getAccountDetails();
    getAccountNotifications();
    setupImageUpload();

    // Display account updates
    displayAccontUpdate();
    bindWidget();
  }

  private void bindWidget() {
    panel.setWidget(view);
  }

  private void setupImageUpload() {
    setImageUploadUrl();
    setUploadImageHandler();
  }

  private void setImageUploadUrl() {
    dispatcher.execute(
        new GetImageUploadUrlAction(),
        new DispatcherCallbackAsync<GetImageUploadUrlResult>(eventBus) {
          @Override
          public void onSuccess(GetImageUploadUrlResult result) {
            view.setImageUploadUrl(result.getImageUrl());
          }
        });
  }

  private void setUploadImageHandler() {
    view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        String imageUrl = event.getResults();
        view.displayProfileImagePreview(imageUrl);
        view.resetImageUploadUrl();
        setImageUploadUrl();
      }
    });
  }

  private void getAccountNotifications() {
    dispatcher
        .execute(new GetAccountNotificationAction(), new AccountNotificationHandler(eventBus));
  }

  public void setLastFetchedTweets(List<TweetDTO> tweets) {
    this.lastTweetList = tweets;
  }

  @Override
  public void go(AcceptsOneWidget container) {
  }

  @Override
  public void bind() {
    view.setPresenter(this);
  }

  @Override
  public void goTo(Place place) {
    eventBus.fireEvent(new PlaceChangeEvent(place));
  }
  
  protected void displayPublicProfile(long accountId) {
    dispatcher.execute(
        new GetAccountByIdAction(accountId),
        new GetAccountHandler<T>(eventBus, view, this));
    fetchTweets(accountId, tweetPageIndex, TWEETS_PER_PAGE, true);
//    startInfiniteScrollThread();
    getPublicAccountDetails(accountId);
    bindWidget();
  }
  
  protected abstract void displayAccontUpdate();

  public void stop() {
    stopThreads();
  }
}
