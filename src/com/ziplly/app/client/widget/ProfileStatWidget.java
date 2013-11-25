package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileStatWidget extends Composite {

	private static ProfileStatWidgetUiBinder uiBinder = GWT
			.create(ProfileStatWidgetUiBinder.class);

	interface ProfileStatWidgetUiBinder extends
			UiBinder<Widget, ProfileStatWidget> {
	}

	public ProfileStatWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTMLPanel panel;
	
	@UiField
	SpanElement title;
	
	@UiField
	SpanElement value;
	
	public void setTitle(String t) {
		title.setInnerHTML(t);
	}

	public void setValue(String v) {
		value.setInnerHTML(v);
	}
	
	public void setWidth(String width) {
		panel.setWidth(width);
	}
}
