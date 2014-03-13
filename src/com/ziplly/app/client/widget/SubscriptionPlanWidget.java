package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class SubscriptionPlanWidget extends Composite {

	private static SubscriptionPlanWidgetUiBinder uiBinder = GWT
	    .create(SubscriptionPlanWidgetUiBinder.class);

	interface SubscriptionPlanWidgetUiBinder extends UiBinder<Widget, SubscriptionPlanWidget> {
	}

	@UiField
	Button buyButton;

	@UiField
	Heading heading;

	@UiField
	Paragraph description;

	@UiField
	HTMLPanel subscriptionPlanPanel;

	public SubscriptionPlanWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setHeading(String text) {
		heading.setText(text);
	}

	public void setDescription(String d) {
		description.setText(d);
	}

	public void addPayButtonClickHandler(ClickHandler handler) {
		buyButton.addClickHandler(handler);
	}

	public void setButtonType(ButtonType type) {
		buyButton.setType(type);
	}

	public void removeBuyButton() {
		StyleHelper.show(buyButton.getElement(), false);
	}

	public void enableBuyButton(boolean enable) {
		buyButton.setEnabled(enable);
	}
}
