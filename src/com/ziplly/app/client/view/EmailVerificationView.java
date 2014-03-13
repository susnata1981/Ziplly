package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class EmailVerificationView extends AbstractView implements
    View<EmailVerificationView.EmailVerificationPresenter> {

	public interface EmailVerificationPresenter extends Presenter {

		void resendEmailVerification(String emailInput);
	}

	private static EmailVerificationViewUiBinder uiBinder = GWT
	    .create(EmailVerificationViewUiBinder.class);

	interface EmailVerificationViewUiBinder extends UiBinder<Widget, EmailVerificationView> {
	}

	@UiField
	Alert message;

	// Email
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;
	@UiField
	TextBox emailTextBox;

	// @UiField
	// Button sendBtn;

	@UiField
	Anchor loginAnchor;

	@UiField
	HTMLPanel successMessagePanel;

	@UiField
	HTMLPanel failureMessagePanel;

	private EmailVerificationPresenter presenter;

	@Inject
	public EmailVerificationView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		clear();
	}

	public void displayMessage(String msg, AlertType success) {
		message.setText(msg);
		message.setType(success);
		message.setVisible(true);
	}

	@UiHandler("loginAnchor")
	public void login(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@Override
	public void setPresenter(EmailVerificationPresenter presenter) {
		this.presenter = presenter;
	}

	public void displaySuccessPanel() {
		StyleHelper.show(successMessagePanel.getElement(), true);
	}

	public void displayFailurePanel() {
		StyleHelper.show(failureMessagePanel.getElement(), true);
	}

	@Override
	public void clear() {
		message.setVisible(false);
		StyleHelper.show(successMessagePanel.getElement(), false);
		StyleHelper.show(failureMessagePanel.getElement(), false);
	}

	public boolean validate() {
		ValidationResult result = FieldVerifier.validateEmail(emailTextBox.getText());
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			return false;
		}

		return true;
	}

	void clearError() {
		emailCg.setType(ControlGroupType.NONE);
		emailError.setText("");
		emailError.setVisible(false);
	}

	@UiHandler("sendBtn")
	public void resendVerificationEmail(ClickEvent handler) {
		clearError();
		if (!validate()) {
			return;
		}

		String emailInput = FieldVerifier.sanitize(emailTextBox.getText());
		presenter.resendEmailVerification(emailInput);
	}
}
