package com.ziplly.app.client.view.home;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.InfiniteScrollHandler;
import com.ziplly.app.client.activities.TweetViewBinder;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.common.action.AccountNotificationHandler;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.EmailAdminAction;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNewMemberAction;
import com.ziplly.app.shared.GetTweetCategoryDetailsAction;

public class HomePresenterImpl implements HomePresenter, InfiniteScrollHandler {
  private static final int MAX_HASHTAG_COUNT = 5;
  private static final int LOOKBACK_DAYS = 90;
  private EventBus eventBus;
  private HomePlace place;
  private PlaceController placeController;
  private ApplicationContext ctx;
  private HomeView view;
  private HomeViewState state;
  private CachingDispatcherAsync dispatcher;
  private TweetViewBinder binder;

  public HomePresenterImpl(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      HomePlace place,
      PlaceController placeController,
      ApplicationContext ctx,
      HomeView view) {
    this.dispatcher = dispatcher;
    this.eventBus = eventBus;
    this.place = place;
    this.placeController = placeController;
    this.ctx = ctx;
    this.view = view;
    this.state = new HomeViewState(ctx.getCurrentNeighborhood());
    bind();
    displayCommunityWall();
  }

  /**
   * Entry point.
   */
  private void displayCommunityWall() {
    setupHandlers();
    setUploadImageHandler();
    getAccountNotifications();
    getCommunityWallData(state.getSearchCriteria(place), false);
    getHashtagList();
    renderMap(ctx.getCurrentNeighborhood());
    getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
    getNeighborhoodDetails();
    getTargetNeighborhoodList();
    getNewMemberList();
  }
  
  @Override
  public void displayCommunityWallForNeighborhood(NeighborhoodDTO neighborhood) {
    getCommunityWallData(state.getSearchCriteria(neighborhood), true);
    getHashtagList();
    getCountsForTweetTypes(state.getCurrentNeighborhood().getNeighborhoodId());
    getNeighborhoodDetails();
  }

  @Override
  public void displayCommunityWall(TweetType type) {
    getCommunityWallData(state.getSearchCriteriaForTweetType(type), true);
  }
  
  private void getNeighborhoodDetails() {
    view.displaySummaryData(state.getCurrentNeighborhood());
    GetNeighborhoodDetailsAction action =
        new GetNeighborhoodDetailsAction(state.getCurrentNeighborhood().getNeighborhoodId());
    dispatcher.execute(action, new NeighborhoodDetailsHandler(eventBus, view));
  }

  private void getHashtagList() {
    GetHashtagAction action =
        new GetHashtagAction(state.getCurrentNeighborhood().getNeighborhoodId());
    action.setSize(MAX_HASHTAG_COUNT);
    dispatcher.execute(action, new GetHashTagHandler(eventBus, view));
  }

  private void getCountsForTweetTypes(long neighborhoodId) {
    dispatcher.execute(
        new GetTweetCategoryDetailsAction(neighborhoodId), 
        new GetTweetCategoryDetailsHandler(eventBus, view));
  };

  private void setImageUploadUrl() {
    dispatcher.execute(new GetImageUploadUrlAction(), new GetImageUploadHandler(eventBus, view));
  }

  // TODO handle image deletion on multiple file uploads
  public void setUploadImageHandler() {
    setImageUploadUrl();
    view.getUploadForm().addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        try {
          String imageUrl = event.getResults();
          view.displayProfileImagePreview(imageUrl);
        } finally {
          view.resetImageUploadUrl();
          setImageUploadUrl();
        }
      }

    });
  }

  private void getNewMemberList() {
    GetNewMemberAction action = new GetNewMemberAction();
    action.setDaysLookback(LOOKBACK_DAYS);
    action.setEntityType(EntityType.PERSONAL_ACCOUNT);
    action.setNeighborhoodId(ctx.getCurrentNeighborhood().getNeighborhoodId());

    dispatcher.execute(action, new GetNewMemberHandler(eventBus, view));
  };

  private void renderMap(NeighborhoodDTO neighborhood) {
    view.displayMap(neighborhood.getPostalCodes().get(0).toString());
  }

  private void setupHandlers() {
    eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {

      @Override
      public void onEvent(AccountDetailsUpdateEvent event) {
        ctx.setAccountDetails(event.getAccountDetails());
        view.setUnreadMessageCount(ctx.getUnreadMessageCount());
      }
    });
  }

  private void getCommunityWallData(GetCommunityWallDataAction action, boolean resetTweets) {
    // Load first batch of data
    state.setFetchingData(true);
    eventBus.fireEvent(new LoadingEventStart());
    dispatcher.execute(action, new CommunityDataHandler(eventBus, state, view, resetTweets));
    startViewBinder();
  }

  private void startViewBinder() {
    if (binder != null) {
//      binder.stop();
      return;
    }

    binder = new TweetViewBinder(view.getTweetSectionElement(), this);// getDefaultTweetBinder();
    binder.start();
  }

  @Override
  public void displayHashtag(String hashtag) {
    if (hashtag != null) {
      GetCommunityWallDataAction searchCriteriaForHashtag =
          state.getSearchCriteriaForHashtag(hashtag);
      getCommunityWallData(searchCriteriaForHashtag, true);
    }
  }

  @Override
  public void displayTweets(List<TweetDTO> tweets) {
    view.display(tweets);
  }

  @Override
  public void gotoBusinessPlace() {
    BusinessPlace place = new BusinessPlace();
    place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
    placeController.goTo(place);
  }

  @Override
  public void gotoResidentPlace() {
    ResidentPlace place = new ResidentPlace();
    place.setNeighborhoodId(state.getCurrentNeighborhood().getNeighborhoodId());
    placeController.goTo(place);
  }

  @Override
  public void sendFeedback(String content) {
    EmailAdminAction action = new EmailAdminAction();
    action.setContent(content);
    action.setSubject(StringConstants.FEEDBACK + " from " + ctx.getAccount().getEmail());
    action.setFrom(ctx.getAccount().getEmail());
    dispatcher.execute(action, new EmailHandler(eventBus, view));
  }

  @Override
  public boolean hasMoreElements() {
    return state.hasMorePages();
  }

  @Override
  public void onScrollBottomHit() {
    if (ctx.getAccount() != null && state.hasMorePages()) {
      GetCommunityWallDataAction nextSearchAction = state.getNextSearchAction();
      state.setFetchingData(true);
      getCommunityWallData(nextSearchAction, false);
    }
  }

  @Override
  public void go(AcceptsOneWidget container) {
    container.setWidget(view);
  }

  @Override
  public void bind() {
    view.setPresenter(this);
  }

  @Override
  public void goTo(Place place) {
    placeController.goTo(place);
  }

  /**
   * Get the list of target neighborhoods in child to parent order.
   */
  private void getTargetNeighborhoodList() {
    view.displayTargetNeighborhoods(ctx.getTargetNeighborhoodList());
  }

  private void getAccountNotifications() {
    dispatcher
        .execute(new GetAccountNotificationAction(), new AccountNotificationHandler(eventBus));
  }

  @Override
  public void getAccountDetails() {
    dispatcher.execute(new GetAccountDetailsAction(), new AccountDetailsHandler(this, eventBus));
  }

  @Override
  public void stop() {
    stopThreads();
  }

  private void stopThreads() {
    if (binder != null) {
      binder.stop();
    }
  }
}
