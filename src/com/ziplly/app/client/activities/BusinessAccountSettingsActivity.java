package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.BusinessAccountSettingsView;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessAccountSettingsActivity extends AbstractAccountSettingsActivity<BusinessAccountDTO>
	implements AccountSettingsPresenter<BusinessAccountDTO>{

	@Inject
	public BusinessAccountSettingsActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx, BusinessAccountSettingsView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (ctx.getAccount() == null) {
			placeController.goTo(new LoginPlace());
		}
		bind();
		setImageUploadFormSubmitCompleteHandler();
		setUploadFormActionUrl();
		view.displaySettings((BusinessAccountDTO)ctx.getAccount());
		panel.setWidget(view);
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void cancel() {
		placeController.goTo(new BusinessAccountPlace());
	}
}
