package com.ziplly.app.client.activities;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.view.NavView.NavPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.AccountNotificationEvent;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.AccountNotificationEventHandler;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.client.view.handler.LogoutEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.SwitchLocationAction;
import com.ziplly.app.shared.SwitchLocationResult;
import com.ziplly.app.shared.ViewNotificationAction;
import com.ziplly.app.shared.ViewNotificationResult;

public class NavActivity extends AbstractActivity implements NavPresenter {
	private INavView view;
	private AccountNotificationHandler accountNotificationHandler = new AccountNotificationHandler();
	private AccountDetailsEventHandler accountDetailsEventHandler = new AccountDetailsEventHandler();

	public static interface INavView extends View<NavPresenter> {
		void showAccountLinks(boolean show);

		void onLogout();

		void clearNotifications();

		NavPresenter getPresenter();

		void displayNoNewNotification();

		void addNotification(AccountNotificationDTO an);

		void displayAccountNotifications(List<AccountNotificationDTO> notifications);

		void updateNotificationCount(int count);

		void setUnreadMessageCount(int count, boolean show);

		void displayLocationsDropdown(List<LocationDTO> locations);

		void displayLocationDropdown(boolean show);

		void displayReportingMenu();
	}

	@Inject
	public NavActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    INavView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		setupHandlers();
	}

	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onEvent(LoginEvent event) {
			  NeighborhoodDTO currentNeighborhood = event.getAccount().getLocations().get(0).getNeighborhood();
				if (FeatureFlags.EnableCouponFeature.isEnabled() 
				    && (event.getAccount() instanceof BusinessAccountDTO)
				    && FeatureFlags.hasPermissionToPublishCoupon(currentNeighborhood)) {
					view.displayReportingMenu();
				}
				onLogin();
				// TODO(susnata): need to enable this.
//				view.displayLocationsDropdown(event.getAccount().getLocations());
			}
		});

		eventBus.addHandler(LogoutEvent.TYPE, new LogoutEventHandler() {

			@Override
			public void onEvent(LogoutEvent event) {
				view.clear();
			}

		});

		eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {

			@Override
			public void onEvent(AccountUpdateEvent event) {
				view.displayLocationsDropdown(event.getAccount().getLocations());
			}

		});

		eventBus.addHandler(AccountNotificationEvent.TYPE, accountNotificationHandler);
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, accountDetailsEventHandler);
	}

	/**
	 * Post login does the following 1. Rpc to GetAccountNotificationActionHandler
	 * and fires AccountNotificationEvent.
	 */
	private void onLogin() {
		view.showAccountLinks(true);
		dispatcher.execute(
		    new GetAccountDetailsAction(),
		    new DispatcherCallbackAsync<GetAccountDetailsResult>() {
			    @Override
			    public void onSuccess(GetAccountDetailsResult result) {
				    eventBus.fireEvent(new AccountNotificationEvent(result.getAccountNotifications()));
				    eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
			    }
		    });
	}

	private void markNotificationAsRead(AccountNotificationDTO an) {
		dispatcher.execute(new ViewNotificationAction(an), new AsyncCallback<ViewNotificationResult>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO
			}

			@Override
			public void onSuccess(ViewNotificationResult result) {
				view.displayAccountNotifications(result.getAccountNotifications());
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// checkAccountLogin();
		bind();
		if (ctx.getAccount() != null) {
			view.showAccountLinks(true);
		}
		panel.setWidget(view);
	}

	@Override
	public void doStart() {
		view.showAccountLinks(true);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void logout() {
		DispatcherCallbackAsync<LogoutResult> dispatcherCallback =
		    new DispatcherCallbackAsync<LogoutResult>() {
			    @Override
			    public void onSuccess(LogoutResult result) {
				    ctx.setAccount(null);
				    eventBus.fireEvent(new LogoutEvent());
				    placeController.goTo(new LoginPlace());
			    }

			    @Override
			    public void onFailure(Throwable t) {
				    t.getMessage();
			    }
		    };
		dispatcher.execute(new LogoutAction(ctx.getAccount().getUid()), dispatcherCallback);
	}

	@Override
	public void redirectToSettingsPage() {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}

		if (account instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountSettingsPlace());
		} else if (account instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountSettingsPlace());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void onNotificationLinkClick(AccountNotificationDTO an) {
		markNotificationAsRead(an);
		switch (an.getType()) {
			case PERSONAL_MESSAGE:
				goTo(new ConversationPlace(an.getConversation().getId()));
				return;
			case SECURITY_ALERT:
			case ANNOUNCEMENT:
			case OFFERS:
			default:
				HomePlace place = new HomePlace(an.getTweet().getTweetId());
				goTo(place);
		}
	}

	private class AccountNotificationHandler implements AccountNotificationEventHandler {
		@Override
		public void onEvent(AccountNotificationEvent event) {
			view.displayAccountNotifications(event.getAccountNotifications());
		}
	}

	private class AccountDetailsEventHandler implements AccountDetailsUpdateEventHandler {

		@Override
		public void onEvent(AccountDetailsUpdateEvent event) {
			view.setUnreadMessageCount(event.getAccountDetails().getUnreadMessages(), true);
		}
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void switchLocation(LocationDTO location) {
		dispatcher.execute(
		    new SwitchLocationAction(location.getLocationId()),
		    new DispatcherCallbackAsync<SwitchLocationResult>() {

			    @Override
			    public void onSuccess(SwitchLocationResult result) {
				    ctx.setAccount(result.getAccount());
				    eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));
				    placeController.goTo(new HomePlace());
			    }

			    @Override
			    public void onFailure(Throwable th) {
				    Window.alert(th.getMessage());
			    }
		    });
	}

	@Override
  public void showTransactions() {
		AccountPlace place = PlaceUtils.getPlace(ctx.getAccount());
		place.setShowTransactions(true);
		goTo(place);
  }
}
