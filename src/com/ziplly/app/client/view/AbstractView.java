package com.ziplly.app.client.view;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AccountFormatter;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;

public abstract class AbstractView extends Composite {
	protected EventBus eventBus;
	protected PlaceController placeController;
	protected BasicDataFormatter basicDataFormatter;
	protected AccountFormatter accountFormatter;
	protected StringDefinitions stringDefinitions = GWT.create(StringDefinitions.class);
	
	public AbstractView(EventBus eventBus) {
		this.eventBus = eventBus;
		basicDataFormatter =
		    (BasicDataFormatter) AbstractValueFormatterFactory
		        .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
		accountFormatter =
		    (AccountFormatter) AbstractValueFormatterFactory
		        .getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION);
	}

	public AbstractView(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController) {
		this(eventBus);
		this.placeController = placeController;
	}

	public void setBackgroundImage(String imageUrl) {
		RootPanel
		    .getBodyElement()
		    .getStyle()
		    .setProperty("background", "url(" + imageUrl + ") no-repeat center center fixed");
		RootPanel.getBodyElement().getStyle().setProperty("backgroundSize", "cover");
	}
}
