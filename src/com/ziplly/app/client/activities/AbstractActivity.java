package com.ziplly.app.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.event.AccountNotificationEvent;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountNotificationResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public abstract class AbstractActivity implements Activity {
	protected CachingDispatcherAsync dispatcher;
	protected PlaceController placeController;
	protected EventBus eventBus;
	protected ApplicationContext ctx;
	private static final String BACKGROUND_IMG_URL = "url('images/neighborhood_large.jpg')";

	public AbstractActivity(CachingDispatcherAsync dispatcher, EventBus eventBus, PlaceController placeController, ApplicationContext ctx) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
	}

	public void goTo(Place place) {
		placeController.goTo(place);
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

	public void setBackgroundImage() {
		RootPanel.get("main").getElement().getStyle().setBackgroundImage(BACKGROUND_IMG_URL);
		RootPanel.get("main").getElement().getStyle().setProperty("backgroundSize", "cover");
	}
	
	public void clearBackgroundImage() {
		RootPanel.get("main").getElement().getStyle().setBackgroundImage("");
	}
	
	public void checkAccountLogin() {
		if (ctx.getAccount() != null) {
			// control shouldn't flow here
			forward(ctx.getAccount());
		}
		GetLoggedInUserAction action = new GetLoggedInUserAction();
		dispatcher.execute(action,
				new DispatcherCallbackAsync<GetLoggedInUserResult>() {
					@Override
					public void onSuccess(GetLoggedInUserResult result) {
						if (result != null && result.getAccount() != null) {
							ctx.setAccount(result.getAccount());
							forward(result.getAccount());
						} else {
							doStart();
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getLocalizedMessage());
					}

				});
	}
	
	public void checkLoginStatus() {
		if (ctx.getAccount() != null) {
			// control shouldn't flow here
			return;
		}
		GetLoggedInUserAction action = new GetLoggedInUserAction();
		dispatcher.execute(action,
				new DispatcherCallbackAsync<GetLoggedInUserResult>() {
					@Override
					public void onSuccess(GetLoggedInUserResult result) {
						if (result != null && result.getAccount() != null) {
							ctx.setAccount(result.getAccount());
							eventBus.fireEvent(new LoginEvent(result.getAccount()));
						} else {
							placeController.goTo(new LoginPlace());
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO (send them to login page?)
						Window.alert(caught.getLocalizedMessage());
					}

				});
	}
	
	protected void doStart() {
	}
	
	protected void forward(AccountDTO acct) {
		if (acct != null) {
			if (acct instanceof PersonalAccountDTO) {
				goTo(new PersonalAccountPlace());
			} else if (acct instanceof BusinessAccountDTO) {
				goTo(new BusinessAccountPlace());
			}
		}
	}
	
	public class AccountNotificationHandler extends DispatcherCallbackAsync<GetAccountNotificationResult> {

		@Override
		public void onSuccess(GetAccountNotificationResult result) {
			eventBus.fireEvent(new AccountNotificationEvent(result.getAccountNotifications()));
		}
	}
}
