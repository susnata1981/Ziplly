package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class NotYetLaunchedWidget extends Composite implements HasClickHandlers {

	private static NotYetLaunchedWidgetUiBinder uiBinder = GWT
			.create(NotYetLaunchedWidgetUiBinder.class);

	interface NotYetLaunchedWidgetUiBinder extends UiBinder<Widget, NotYetLaunchedWidget> {
	}

	public NotYetLaunchedWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Modal modal;
	
	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;
	
	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;
	
	@UiField
	Button submitBtn;
	
	@UiField
	Button cancelBtn;

	public NotYetLaunchedWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		email.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				validateEmail();
			}
		});
		
		zip.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				validateZip();
			}
		});
	}

	@UiHandler("cancelBtn")
	void onClick(ClickEvent e) {
		modal.hide();
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return submitBtn.addClickHandler(handler);
	}

	public void show(boolean visible) {
		if (visible) {
			modal.show();
		} else {
			modal.hide();
		}
	}
	
	boolean validateEmail() {
		String emailInput = email.getText().trim();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			displayValidationMessage(emailError, emailCg, result.getErrors().get(0).getErrorMessage(), true, ControlGroupType.ERROR);
			return false;
		}
		return true;
	}

	public String getEmail() {
		return email.getText();
	}
	
	public void displayValidationMessage(HelpInline field, ControlGroup cg, String errorMsg, boolean isVisible, ControlGroupType type) {
		if (isVisible) {
			field.setVisible(true);
			field.setText(errorMsg);
		} else {
			field.setVisible(false);
		}
		cg.setType(type);
	}
	
	boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			displayValidationMessage(zipError, zipCg, validateZip.getErrors().get(0).getErrorMessage(), true, ControlGroupType.ERROR);
			return false;
		}
		
		return true;
	}
	
	public boolean validateInput() {
		boolean valid = validateEmail();
		valid &= validateZip();
		return valid;
	}

	public String getPostalCode() {
		return FieldVerifier.sanitize(zip.getText());
	}
}
