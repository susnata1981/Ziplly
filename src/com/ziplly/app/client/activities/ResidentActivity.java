package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.shared.GetResidentsRequest;
import com.ziplly.app.shared.GetResidentsResult;

public class ResidentActivity extends AbstractActivity {
	private ResidentsView view;
	private Object place;
	private AcceptsOneWidget panel;

	@Inject
	public ResidentActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			ResidentsView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		if (ctx.getAccount() != null) {
			display();
		} else {
			goTo(new LoginPlace());
		}
	}

	void display() {
		GetResidentsRequest req = new GetResidentsRequest(ctx.getAccount());
		dispatcher.execute(req, new DispatcherCallbackAsync<GetResidentsResult>() {
			@Override
			public void onSuccess(GetResidentsResult result) {
				view.display(result.getAccounts());
				panel.setWidget(view);
			}
			
			public void onFailure(Throwable caught) {
				Throwable c = caught;
			}
		});
	}
	
}
