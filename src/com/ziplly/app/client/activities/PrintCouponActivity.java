package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.ErrorDefinitions;
import com.ziplly.app.client.places.PrintCouponPlace;
import com.ziplly.app.client.view.PrintCouponView;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class PrintCouponActivity extends AbstractActivity {
	private PrintCouponView view;
	private AcceptsOneWidget panel;
	private PrintCouponPlace place;
	private AsyncProvider<PrintCouponView> viewProvider;

	public PrintCouponActivity(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      PrintCouponPlace place,
      AsyncProvider<PrintCouponView> view) {
	  super(dispatcher, eventBus, placeController, ctx);
	  this.place = place;
	  this.viewProvider = view;
  }

	@Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
  }

	@Override
  protected void doStart() {
		viewProvider.get(new AsyncCallback<PrintCouponView>() {

			@Override
      public void onFailure(Throwable caught) {
				Window.alert("Sorry, failed to load coupon. Please try again.");
      }

			@Override
      public void onSuccess(PrintCouponView result) {
				PrintCouponActivity.this.view = result;
				panel.setWidget(view);
				printCoupon(place.getCouponTransactionId());
      }
		});
  }

	private void printCoupon(Long transactionId) {
		if (transactionId == null || transactionId <= 0) {
			view.displayMessage(
			    ErrorDefinitions.invalidCouponError.getErrorMessage(),
			    ErrorDefinitions.invalidCouponError.getType());
			return;
		}

		GetCouponQRCodeUrlAction action = new GetCouponQRCodeUrlAction();
		action.setCouponTransactionId(transactionId);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetCouponQRCodeUrlResult>() {

			@Override
			public void onSuccess(GetCouponQRCodeUrlResult result) {
				view.displayCoupon(result);
			}
		});
	}
	
	@Override
	public void onStop() {
		view.reset();
	}

}
