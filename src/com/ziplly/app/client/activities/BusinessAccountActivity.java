package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class BusinessAccountActivity extends
		AbstractAccountActivity<BusinessAccountDTO> {

	private BusinessAccountPlace place;
	private AcceptsOneWidget panel;

	public BusinessAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, IAccountView<BusinessAccountDTO> view,
			BusinessAccountPlace place) {
		super(dispatcher, eventBus, placeController, ctx, view);
		this.place = place;
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		if (place.getAccountId() != null) {
			displayProfile(place.getAccountId());
			return;
		}

		if (ctx.getAccount() != null) {
			displayProfile();
			go(panel);
		} else {
			goTo(new LoginPlace());
		}
	}

	public void displayProfile(final Long accountId) {
		dispatcher.execute(new GetAccountByIdAction(accountId),
				new DispatcherCallbackAsync<GetAccountByIdResult>() {
					@Override
					public void onSuccess(GetAccountByIdResult result) {
						AccountDTO account = result.getAccount();
						if (account instanceof BusinessAccountDTO) {
							view.displayPublicProfile((BusinessAccountDTO) account);
							go(panel);
						} else {
							placeController.goTo(new PersonalAccountPlace(accountId));
						}
					}
					
					@Override
					public void onFailure(Throwable t) {
						System.out.println("No account found with id:"+accountId);
						view.displayMessage(StringConstants.NO_ACCOUNT_FOUND, AlertType.ERROR);
						go(panel);
					}
				});
	}

	@Override
	public void displayProfile() {
		view.displayProfile((BusinessAccountDTO) ctx.getAccount());
	}
	
	@Override
	public void settingsLinkClicked() {
		placeController.goTo(new BusinessAccountSettingsPlace());
	}

	@Override
	protected void onAccountDetailsUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayPublicProfile(Long accountId) {
		// TODO Auto-generated method stub
		
	}
}
