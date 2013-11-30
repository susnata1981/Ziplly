package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.ZGinInjector;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;

public class BusinessAccountWidgetModal extends Composite implements
		IAccountWidgetModal<BusinessAccountDTO> {

	private static BusinessAccountWidgetModalUiBinder uiBinder = GWT
			.create(BusinessAccountWidgetModalUiBinder.class);

	interface BusinessAccountWidgetModalUiBinder extends
			UiBinder<Widget, BusinessAccountWidgetModal> {
	}

	@UiField
	Modal accountWidgetModal;

	@UiField
	Image profileImageUrl;

	@UiField
	Element name;

	@UiField
	Button viewProfileBtn;

	@UiField
	Button cancelBtn;

	@UiField
	SpanElement address;

	private Presenter presenter;

	private BusinessAccountDTO account;
	CachingDispatcherAsync dispatcher;

	public BusinessAccountWidgetModal() {
		ZGinInjector injector = GWT.create(ZGinInjector.class);
		dispatcher = injector.getCachingDispatcher();
		initWidget(uiBinder.createAndBindUi(this));
		setWidth("25%");
		hide();
	}

	public void setWidth(String width) {
		accountWidgetModal.setWidth(width);
	}
	
	private void setupHandlers() {
		viewProfileBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new BusinessAccountPlace(account.getAccountId()));
				hide();
			}
		});

		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	@Override
	public void hide() {
		accountWidgetModal.hide();
	}

	@Override
	public void show(BusinessAccountDTO account) {
		this.account = account;
		setupHandlers();
		displayAccount(account);
		accountWidgetModal.show();
		getLatLng(account, new GetLatLngResultHandler());
	}

	public void displayAccount(BusinessAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		name.setInnerHTML(account.getName());
		profileImageUrl.setUrl(account.getImageUrl());
	}

//	public void displayLocationInMap(GetLatLngResult input) {
//		LatLng myLatLng = LatLng.create(input.getLat(), input.getLng());
//		MapOptions myOptions = MapOptions.create();
//		myOptions.setZoom(10.0);
//		myOptions.setCenter(myLatLng);
//		myOptions.setMapMaker(true);
//		myOptions.setMapTypeId(MapTypeId.ROADMAP);
//
//		GoogleMap map = GoogleMap.create(locationDiv, myOptions);
//		MarkerOptions markerOpts = MarkerOptions.create();
//		markerOpts.setMap(map);
//		markerOpts.setPosition(myLatLng);
//		Marker.create(markerOpts);
//	}

	void displayFormattedAddress(GetLatLngResult result) {
		address.setInnerHTML(result.getFormattedAddress());
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	void getLatLng(AccountDTO account, DispatcherCallbackAsync<GetLatLngResult> handler) {
		GetLatLngAction action = new GetLatLngAction();
		action.setAccount(account);
		dispatcher.execute(action, handler);
	}

	private class GetLatLngResultHandler extends DispatcherCallbackAsync<GetLatLngResult> {
		@Override
		public void onSuccess(GetLatLngResult result) {
			displayFormattedAddress(result);
		}
	}
}
