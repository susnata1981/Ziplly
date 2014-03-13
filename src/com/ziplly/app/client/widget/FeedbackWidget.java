package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class FeedbackWidget extends Composite {
	private static FeedbackWidgetUiBinder uiBinder = GWT.create(FeedbackWidgetUiBinder.class);

	interface FeedbackWidgetUiBinder extends UiBinder<Widget, FeedbackWidget> {
	}

	public FeedbackWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		feedbackModal.hide();
		StyleHelper.show(message.getElement(), false);
	}

	@UiField
	Alert message;
	@UiField
	Modal feedbackModal;
	@UiField
	TextArea feedbackTextArea;
	@UiField
	Button submitBtn;
	@UiField
	Button closeBtn;

	public void show(boolean show) {
		StyleHelper.show(message.getElement(), false);
		if (show) {
			feedbackModal.show();
		} else {
			feedbackModal.hide();
		}
	}

	public ValidationResult validate() {
		return FieldVerifier.validateMessage(feedbackTextArea.getText());
	}

	public String getContent() {
		return FieldVerifier.sanitize(feedbackTextArea.getText());
	}

	public Button getSubmitButton() {
		StyleHelper.show(message.getElement(), false);
		return submitBtn;
	}

	public Button closeButton() {
		return closeBtn;
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}

	@UiHandler("closeBtn")
	public void close(ClickEvent event) {
		feedbackModal.hide();
	}

	public void clear() {
		StyleHelper.show(message.getElement(), false);
	}
}
