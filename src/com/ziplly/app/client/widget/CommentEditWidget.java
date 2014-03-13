package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class CommentEditWidget extends Composite implements HasText {

	private static CommentEditWidgetUiBinder uiBinder = GWT.create(CommentEditWidgetUiBinder.class);

	interface CommentEditWidgetUiBinder extends UiBinder<Widget, CommentEditWidget> {
	}

	@UiField
	TextArea editTextArea;

	@UiField
	Button saveBtn;

	@UiField
	Button cancelBtn;

	@UiField
	Alert message;

	public CommentEditWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		StyleHelper.show(message.getElement(), false);
	}

	@Override
	public String getText() {
		return FieldVerifier.sanitize(editTextArea.getText());
	}

	@Override
	public void setText(String text) {
		editTextArea.setText(text);
	}

	public Button getSaveButton() {
		return saveBtn;
	}

	public Button getCancelButton() {
		return cancelBtn;
	}

	public boolean validateInput() {
		ValidationResult result = FieldVerifier.validateComment(editTextArea.getText());
		if (!result.isValid()) {
			displayMessage(result.getErrors().get(0).getErrorMessage(), AlertType.ERROR);
		}
		return result.isValid();
	}

	public void showMessage(boolean b) {
		StyleHelper.show(message.getElement(), b);
	}

	public void displayMessage(String text, AlertType type) {
		message.setText(text);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
}
