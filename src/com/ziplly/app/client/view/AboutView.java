package com.ziplly.app.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.widget.MyBundle;

public class AboutView extends Composite {

	private static AboutViewUiBinder uiBinder = GWT.create(AboutViewUiBinder.class);

	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	@UiField
	MyBundle resource;
	
	@UiField
	ImageElement tom;
	
	@UiFactory
	MyBundle getResource() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}
	
	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
		tom.setSrc(resource.tom().getSafeUri().asString());
	}
}
