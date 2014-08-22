package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.account.PersonalAccountSettingsPresenterImpl;
import com.ziplly.app.client.view.account.PersonalAccountSettingsView;

public class PersonalAccountSettingsActivity extends AbstractActivity {
	private AcceptsOneWidget panel;
	private AsyncProvider<PersonalAccountSettingsView> viewProvider;

	public PersonalAccountSettingsActivity(
	    CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<PersonalAccountSettingsView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
	}

  @Override
  public String mayStop() {
    return null;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onStop() {
  }

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }

  @Override
  protected void doStart() {
    viewProvider.get(new DefaultViewLoaderAsyncCallback<PersonalAccountSettingsView>() {

      @Override
      public void onSuccess(PersonalAccountSettingsView view) {
        new PersonalAccountSettingsPresenterImpl(dispatcher, eventBus, placeController, ctx, panel, view);
      }
    });
  }
}
