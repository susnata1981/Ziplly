package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.ValidationResult;

public class PasswordRecoveryView extends Composite implements
		View<PasswordRecoveryView.PasswordRecoveryPresenter> {

	private static PasswordRecoveryViewUiBinder uiBinder = GWT
			.create(PasswordRecoveryViewUiBinder.class);

	interface PasswordRecoveryViewUiBinder extends
			UiBinder<Widget, PasswordRecoveryView> {
	}

	public static interface PasswordRecoveryPresenter extends Presenter {
		void emailPasswordResetLink(String email);

		void resetPassword(ResetPasswordAction action);
	}

	@UiField
	Alert message;
	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

	// Reset Password tab
	@UiField
	PasswordTextBox newPassword;
	@UiField
	ControlGroup newPasswordCg;
	@UiField
	HelpInline newPasswordError;

	@UiField
	PasswordTextBox confirmNewPassword;
	@UiField
	ControlGroup confirmNewPasswordCg;
	@UiField
	HelpInline confirmNewPasswordError;

	@UiField
	Button resetPasswordBtn;
	@UiField
	Button cancelBtn;
	
	@UiField
	HTMLPanel passwordResetPanel;
	@UiField
	HTMLPanel passwordRecoveryPanel;
	
	PasswordRecoveryPresenter presenter;

	public PasswordRecoveryView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
	}

	@Override
	public void setPresenter(PasswordRecoveryPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		message.clear();
		message.setVisible(false);
		newPassword.setText("");
		confirmNewPassword.setText("");
	}

	boolean validate() {
		String emailInput = email.getText().trim();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			return false;
		}
		return true;
	}

	@UiHandler("submitBtn")
	public void emailPasswordResetLink(ClickEvent event) {
		if (!validate()) {
			return;
		}
		presenter.emailPasswordResetLink(FieldVerifier.sanitize(email.getText()));
	}

	public void showMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	public void displayPasswordResetForm() {
		clear();
		passwordRecoveryPanel.getElement().getStyle().setDisplay(Display.NONE);
		passwordResetPanel.getElement().getStyle().setDisplay(Display.BLOCK);
	}
	
	public void displayPasswordRecoveryForm() {
		clear();
		passwordResetPanel.getElement().getStyle().setDisplay(Display.NONE);
		passwordRecoveryPanel.getElement().getStyle().setDisplay(Display.BLOCK);
	}
	
	@UiHandler("resetPasswordBtn")
	void resetPassword(ClickEvent event) {
		resetPasswordErrors();
		if (!validatePasswordInput()) {
			return;
		}
		
		String newPasswordInput = FieldVerifier.sanitize(newPassword.getText());
		ResetPasswordAction action = new ResetPasswordAction();
		action.setPassword(newPasswordInput);
		presenter.resetPassword(action);
		clear();
	}
	
	@UiHandler("cancelBtn")
	void cancelBtnClicked(ClickEvent event) {
		presenter.goTo(new HomePlace());
	}
	
	boolean validatePassword(String password, ControlGroup cg,
			HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validatePassword(password);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}
	
	private boolean validatePasswordInput() {
		String newPasswordInput = newPassword.getText().trim();
		boolean valid = validatePassword(newPasswordInput, newPasswordCg,
				newPasswordError);
		
		String confirmPasswordInput = confirmNewPassword.getText().trim();
		valid &= validatePassword(confirmPasswordInput, confirmNewPasswordCg,
				confirmNewPasswordError);

		if (newPasswordInput != null && confirmPasswordInput != null) {
			if (!confirmPasswordInput.equals(newPasswordInput)) {
				confirmNewPasswordCg.setType(ControlGroupType.ERROR);
				confirmNewPasswordError.setText(StringConstants.PASSWORD_MISMATCH_ERROR);
				confirmNewPasswordError.setVisible(true);
			}
		}
		return valid;
	}

	void resetPasswordErrors() {
		message.clear();
		message.setVisible(false);
		newPasswordCg.setType(ControlGroupType.NONE);
		newPasswordError.setVisible(false);
		confirmNewPasswordCg.setType(ControlGroupType.NONE);
		confirmNewPasswordError.setVisible(false);
	}
}
