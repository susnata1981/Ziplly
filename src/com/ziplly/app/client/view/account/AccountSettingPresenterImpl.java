package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AccountSwitcherPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdatePasswordAction;

public abstract class AccountSettingPresenterImpl<T extends AccountDTO, P extends AccountSettingsPresenter<T>> 
  implements AccountSettingsPresenter<T> {
  
  protected CachingDispatcherAsync dispatcher;
  protected EventBus eventBus;
  protected PlaceController placeController;
  protected ApplicationContext ctx;
  protected AcceptsOneWidget panel;
  protected ISettingsView<T, P> view;

  public AccountSettingPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      AcceptsOneWidget panel,
      ISettingsView<T, P> view) {
    
    this.dispatcher = dispatcher;
    this.eventBus = eventBus;
    this.placeController = placeController;
    this.ctx = ctx;
    this.panel = panel;
    this.view = view;
    bind();
    bindWidget();
    setupHandlers();
  }
  
  private void setupHandlers() {
    eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
      @Override
      public void onEvent(LoginEvent event) {
        ctx.setAccount(event.getAccount());
      }
    });

    eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {

      @Override
      public void onEvent(AccountUpdateEvent event) {
        view.displaySettings((T) ctx.getAccount());
      }

    });
  }

  private void bindWidget() {
    this.panel.setWidget(view);
  }

  public void save(T account) {
    if (account == null) {
      throw new IllegalArgumentException();
    }
    
    eventBus.fireEvent(new LoadingEventStart());
    dispatcher.execute(new UpdateAccountAction(account), new UpdateAccountHandler(eventBus, view, ctx));
  }

//  public void updatePassword(UpdatePasswordAction action,
//      DispatcherCallbackAsync<UpdatePasswordResult> callback) {
//    dispatcher.execute(action, callback);
//  }

  public void setUploadFormActionUrl() {
//    dispatcher.execute(
//        new GetImageUploadUrlAction(),
//        new DispatcherCallbackAsync<GetImageUploadUrlResult>(eventBus) {
//          @Override
//          public void onSuccess(GetImageUploadUrlResult result) {
//            // TODO hack for making it work in local environment
//            String url =
//                result.getImageUrl().replace("susnatas-MacBook-Pro.local:8888", "127.0.0.1:8888");
//            System.out.println("Setting upload image form action to:" + url);
//            view.setUploadFormActionUrl(url);
//          }
//          
//          @Override
//          public void onFailure(Throwable th) {
//            Window.alert("Failed to set upload link");
//          }
//        });
    dispatcher.execute(new GetImageUploadUrlAction(), new GetImageUrlUploadHandler(eventBus, view));
  }

  // TODO handle image deletion on multiple file uploads
  public void setImageUploadFormSubmitCompleteHandler() {
    view.setUploadFormSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
      
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        String imageUrl = event.getResults();
        System.out.println("Received uploaded image url:" + imageUrl);
        if (imageUrl == null || "".equals(imageUrl)) {
          view.displayMessage(StringConstants.INVALID_IMAGE, AlertType.ERROR);
        } else {
          view.displayImagePreview(imageUrl);
        }
        resetUploadForm();
        eventBus.fireEvent(new LoadingEventEnd());
      }
      
    });
  }

  void resetUploadForm() {
    view.resetUploadForm();
    setUploadFormActionUrl();
  }

  @Override
  public void cancel() {
    goTo(new AccountSwitcherPlace(ctx.getAccount().getAccountId()));
  }

  @Override
  public void updatePassword(UpdatePasswordAction action) {
    dispatcher.execute(action, new UpdatePasswordHandler(eventBus, view));
  }

  @Override
  public void onProfileLinkClick() {
    goTo(new AccountSwitcherPlace());
  }

  @Override
  public void onInboxLinkClick() {
    goTo(new ConversationPlace());
  }

  @Override
  public void go(AcceptsOneWidget container) {
  }

  @Override
  public void goTo(Place place) {
    placeController.goTo(place);
  }

}
