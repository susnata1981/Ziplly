package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificationWidget extends Composite {

	private static NotificationWidgetUiBinder uiBinder = GWT
			.create(NotificationWidgetUiBinder.class);

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationWidget> {
	}

	public NotificationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button accountSettingsBtn;
	
	@UiField
	Button closeBtn;
	
	@UiField
	HTMLPanel panel;
	
	public Button getAccountSettingButton() {
		return accountSettingsBtn;
	}

	public Button getCloseButton() {
		return closeBtn;
	}
	
//	@UiHandler("closeBtn")
//	public void close(ClickEvent event) {
//		System.out.println("CALLING CLOSE");
//		panel.hide(true);
//	}

	
}
