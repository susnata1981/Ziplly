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
	
	public AlertModal() {
		initWidget(uiBinder.createAndBindUi(this));
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
		timer.schedule(1000);
	}
	
}
