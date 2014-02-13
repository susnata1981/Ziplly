package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.PersonalAccountSettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetInterestAction;
import com.ziplly.app.shared.GetInterestResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class PersonalAccountSettingsActivity
		extends
		AbstractAccountSettingsActivity<PersonalAccountDTO, PersonalAccountSettingsActivity.IPersonalAccountSettingsView>
		implements AccountSettingsPresenter<PersonalAccountDTO> {

	public static interface IPersonalAccountSettingsView extends
			ISettingsView<PersonalAccountDTO, AccountSettingsPresenter<PersonalAccountDTO>> {

		void displayAllInterests(List<InterestDTO> interests);
	}

	private AcceptsOneWidget panel;
	private AsyncProvider<PersonalAccountSettingsView> viewProvider;

	public PersonalAccountSettingsActivity(
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController, 
			ApplicationContext ctx,
			AsyncProvider<PersonalAccountSettingsView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx, null);
		this.viewProvider = viewProvider;
		setupHandlers();
	}

	protected void setupHandlers() {
		super.setupHandlers();
		
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				ctx.setAccount(event.getAccount());
				internalStart();
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if (ctx.getAccount() == null) {
			checkLoginStatus();
			return;
		} else {
			internalStart();
		}
	}

	private void internalStart() {
		// hack, hate hate hate
		if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountSettingsPlace());
			return;
		}

		viewProvider.get(new AsyncCallback<PersonalAccountSettingsView>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(PersonalAccountSettingsView result) {
				PersonalAccountSettingsActivity.this.view = result;
				bind();
			}
		});
		
		setImageUploadFormSubmitCompleteHandler();
		setUploadFormActionUrl();
		view.displaySettings((PersonalAccountDTO) ctx.getAccount());
		fetchInterestList();
		panel.setWidget(view);
	}

	private void fetchInterestList() {
		dispatcher.execute(new GetInterestAction(), new DispatcherCallbackAsync<GetInterestResult>() {

			@Override
			public void onSuccess(GetInterestResult result) {
				view.displayAllInterests(result.getInterests());
			}
		});
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void cancel() {
		placeController.goTo(new PersonalAccountPlace());
	}

	@Override
	public void updatePassword(UpdatePasswordAction action) {
		updatePassword(action, new DispatcherCallbackAsync<UpdatePasswordResult>() {
			@Override
			public void onSuccess(UpdatePasswordResult result) {
				view.displayMessage(StringConstants.PASSWORD_UPDATED, AlertType.SUCCESS);
			}

			public void onFailure(Throwable th) {
				if (th instanceof InvalidCredentialsException) {
					view.displayMessage(th.getMessage(), AlertType.ERROR);
				} else {
					view.displayMessage(StringConstants.PASSWORD_UPDATE_FAILURE, AlertType.ERROR);
				}
			}
		});
	}

	@Override
	public void onProfileLinkClick() {
		placeController.goTo(new PersonalAccountPlace());
	}

	@Override
	public void onInboxLinkClick() {
		placeController.goTo(new ConversationPlace());
	}
	
	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

}
