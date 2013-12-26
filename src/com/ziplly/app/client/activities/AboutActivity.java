package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AboutView;

public class AboutActivity extends AbstractActivity {
	private AboutView view;
	
	@Inject
	public AboutActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, AboutView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);
	}

}
