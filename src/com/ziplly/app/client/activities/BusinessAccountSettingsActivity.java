package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.view.account.BusinessAccountSettingsPresenterImpl;
import com.ziplly.app.client.view.account.BusinessAccountSettingsView;

public class BusinessAccountSettingsActivity extends AbstractActivity {
	private AsyncProvider<BusinessAccountSettingsView> viewProvider;
  private BusinessAccountSettingsPlace place;
  private AcceptsOneWidget panel;

	@Inject
	public BusinessAccountSettingsActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<BusinessAccountSettingsView> viewProvider,
	    BusinessAccountSettingsPlace place) {
		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
		this.place = place;
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
  public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }

  @Override
  protected void doStart() {
    viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessAccountSettingsView>() {

      @Override
      public void onSuccess(BusinessAccountSettingsView view) {
        new BusinessAccountSettingsPresenterImpl(dispatcher, eventBus, placeController, ctx, panel, view, place);
      }
    });
  }
}
