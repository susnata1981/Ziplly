package com.ziplly.app.client.widget;

import java.util.Arrays;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.EmailPresenter;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class EmailWidget extends Composite {
	private static final String COMMA_SEPARATOR = ",";

	private static EmailWidgetUiBinder uiBinder = GWT.create(EmailWidgetUiBinder.class);
	
	@UiField
	Alert message;
	
	@UiField
	TextBox emailList;
	
	@UiField
	Button invitePeopleBtn;
	
	@UiField
	Button cancelBtn;
	
	@UiField
	Modal emailWidgetModal;

	private EmailPresenter presenter;

	interface EmailWidgetUiBinder extends UiBinder<Widget, EmailWidget> {
	}

	public EmailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		emailWidgetModal.hide();
	}

	@UiHandler("invitePeopleBtn")
	void sendEmail(ClickEvent event) {
		clear();
		if (!validate()) {
			return;
		}
		String[] emails = emailList.getText().split(COMMA_SEPARATOR);
		presenter.invitePeople(Arrays.asList(emails));
		emailList.setText("");
		hide();
	}

	@UiHandler("cancelBtn")
	public void cancel(ClickEvent event) {
		emailWidgetModal.hide();
	}
	
	private boolean validate() {
		String input = emailList.getText();
		ValidationResult result = FieldVerifier.validateEmailList(input);
		if (!result.isValid()) {
			displayMessage(result.getErrors().get(0).getErrorMessage());
			return false;
		}
		return true;
	}

	public void displayMessage(String errorMessage) {
		message.setText(errorMessage);
		message.setType(AlertType.ERROR);
		message.setVisible(true);
	}

	public void show() {
		emailWidgetModal.show();
	}
	
	public void hide() {
		emailWidgetModal.hide();
	}
	
	public void clear() {
		message.setVisible(false);
	}
	
	public void setPresenter(EmailPresenter presenter) {
		this.presenter = presenter;
	}
}