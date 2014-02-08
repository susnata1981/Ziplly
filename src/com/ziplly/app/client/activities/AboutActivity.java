package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.view.AboutView;
import com.ziplly.app.client.view.AboutView.AboutPresenter;
import com.ziplly.app.client.view.AboutViewSection;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.shared.EmailAdminAction;
import com.ziplly.app.shared.EmailAdminResult;

public class AboutActivity extends AbstractActivity implements AboutPresenter{
	private AboutView view;
	private AboutPlace place;
	
	@Inject
	public AboutActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, AboutPlace place, AboutView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.view = view;
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		bind();
		view.displaySection(AboutViewSection.valueOf(place.getSection()));
		panel.setWidget(view);
	}

	@Override
	public void contact(String subject, String from, String content) {
		EmailAdminAction action = new EmailAdminAction(from, content, subject);
		dispatcher.execute(action, new DispatcherCallbackAsync<EmailAdminResult>() {

			@Override
			public void onSuccess(EmailAdminResult result) {
				view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
				view.clear();
			}
		});
	}

	@Override
	public void fetchData() {
	}


	@Override
	public void go(AcceptsOneWidget container) {
	}


	@Override
	public void bind() {
		view.setPresenter(this);
	}
}
