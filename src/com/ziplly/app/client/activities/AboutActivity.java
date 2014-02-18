package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	private AsyncProvider<AboutView> viewProvider;
	private AcceptsOneWidget panel;
	
	@Inject
	public AboutActivity(
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController, 
			ApplicationContext ctx, 
			AboutPlace place, 
			AsyncProvider<AboutView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		viewProvider.get(new AsyncCallback<AboutView>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(AboutView result) {
				AboutActivity.this.view = result;
				bind();
				view.displaySection(AboutViewSection.valueOf(place.getSection()));
				AboutActivity.this.panel.setWidget(view);
			}
		});
	}

	@Override
	protected void doStart() {
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
	public void go(AcceptsOneWidget container) {
	}


	@Override
	public void bind() {
		view.setPresenter(this);
	}
}
