package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AlertModal extends Composite {

	private static AlertModalUiBinder uiBinder = GWT.create(AlertModalUiBinder.class);

	interface AlertModalUiBinder extends UiBinder<Widget, AlertModal> {
	}

	@UiField
	Modal modal;

	@UiField
	Alert alert;

	private int displayTime;
	
	public AlertModal() {
		initWidget(uiBinder.createAndBindUi(this));
//		String property = System.getProperty(StringConstants.DISPLAY_MESSAGE_VIEW_TIME, "1000");
//		displayTime = Integer.parseInt(property);
		displayTime = 3000;
	}

	public void showMessage(String msg, AlertType type) {
		alert.setText(msg);
		alert.setType(type);
		modal.show();
		Timer timer = new Timer() {

			@Override
			public void run() {
				modal.hide();
			}
		};
		timer.schedule(displayTime);
	}
}
