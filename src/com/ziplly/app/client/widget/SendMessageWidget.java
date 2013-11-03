package com.ziplly.app.client.widget;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class SendMessageWidget extends Composite implements View<AccountPresenter<?>>{

	private static SendMessageWidgetUiBinder uiBinder = GWT
			.create(SendMessageWidgetUiBinder.class);

	interface SendMessageWidgetUiBinder extends
			UiBinder<Widget, SendMessageWidget> {
	}

	@UiField
	TextBox subject;
	@UiField
	ControlGroup subjectCg;
	@UiField
	HelpInline subjectHelpInline;
	
	@UiField
	ControlGroup messageCg;
	@UiField
	HelpInline messageHelpInline;
	@UiField
	TextArea message;
	
	@UiField
	SubmitButton sendBtn;
	
	@UiField
	Alert status;
	
	@UiField
	HTMLPanel rootPanel;
	
	@UiField
	Modal modal;
	private AccountPresenter<?> presenter;
	private AccountDTO receiver;
	
	public SendMessageWidget(AccountDTO receiver) {
		this.receiver = receiver;
		initWidget(uiBinder.createAndBindUi(this));
		modal.hide();
		subjectHelpInline.setVisible(false);
		messageHelpInline.setVisible(false);
		status.setVisible(false);
	}
	
	public void show() {
		modal.show();
	}
	
	public void hide() {
		modal.hide();
	}

	boolean validate() {
		String subjectInput = FieldVerifier.getEscapedText(subject.getText());
		ValidationResult result = FieldVerifier.validateName(subjectInput);
		if (!result.isValid()) {
			subjectCg.setType(ControlGroupType.ERROR);
			subjectHelpInline.setText(result.getErrors().get(0).getErrorMessage());
			subjectHelpInline.setVisible(true);
			return false;
		}

		String messageInput = FieldVerifier.getEscapedText(message.getText());
		result = FieldVerifier.validateName(messageInput);
		if (!result.isValid()) {
			messageCg.setType(ControlGroupType.ERROR);
			messageHelpInline.setText(result.getErrors().get(0).getErrorMessage());
			messageHelpInline.setVisible(true);
			return false;
		}
		return true;
	}
	
	@UiHandler("sendBtn")
	void send(ClickEvent event) {
		if (!validate()) {
			return;
		}
		ConversationDTO conversation = new ConversationDTO();
		MessageDTO msg = new MessageDTO();
		msg.setReceiver(receiver);
		msg.setMessage(FieldVerifier.getEscapedText(message.getText().trim()));
		msg.setTimeCreated(new Date());
		conversation.setSubject(FieldVerifier.getEscapedText(subject.getText().trim()));
		conversation.setReceiver(receiver);
		conversation.setTimeCreated(new Date());
		conversation.setTimeUpdated(new Date());
		conversation.add(msg);
		presenter.sendMessage(conversation);
	}

	@UiHandler("closeBtn")
	void close(ClickEvent event) {
		hide();
	}
	
	public void setStatus(String msg, AlertType type) {
		status.setText(msg);
		status.setType(type);
		status.setVisible(true);
	}

	@Override
	public void clear() {
		subject.setText("");
		message.setText("");
		subjectHelpInline.setVisible(false);
		messageHelpInline.setVisible(false);
		status.setVisible(false);
	}

	@Override
	public void setPresenter(AccountPresenter<?> presenter) {
		this.presenter = presenter;
	}
}
