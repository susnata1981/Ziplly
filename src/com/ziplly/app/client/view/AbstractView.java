package com.ziplly.app.client.view;

import java.util.logging.Logger;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AccountFormatter;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;

public abstract class AbstractView extends Composite {
	protected Logger logger = Logger.getLogger("AbstractView");
	protected EventBus eventBus;
	protected PlaceController placeController;
	protected BasicDataFormatter basicDataFormatter;
	protected AccountFormatter accountFormatter;
	
	public AbstractView(EventBus eventBus) {
		this.eventBus = eventBus;
		basicDataFormatter = (BasicDataFormatter) AbstractValueFormatterFactory.getValueFamilyFormatter(
				ValueFamilyType.BASIC_DATA_VALUE);
		accountFormatter = (AccountFormatter)AbstractValueFormatterFactory.getValueFamilyFormatter(
				ValueFamilyType.ACCOUNT_INFORMATION);
	}
	
	public AbstractView(CachingDispatcherAsync dispatcher,
			EventBus eventBus,
			PlaceController placeController) {
		this(eventBus);
		this.placeController = placeController;
	}

	
}
