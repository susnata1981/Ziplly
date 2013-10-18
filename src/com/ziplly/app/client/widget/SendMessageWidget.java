package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.ZipllyService;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Message;

public class SendMessageWidget extends AbstractView {

	private static SendMessageWidgetUiBinder uiBinder = GWT
			.create(SendMessageWidgetUiBinder.class);

	interface SendMessageWidgetUiBinder extends
			UiBinder<Widget, SendMessageWidget> {
	}

	@UiField
	TextBox subject;
	
	@UiField
	TextArea message;
	
	@UiField
	SubmitButton sendBtn;
	
	@UiField
	HTMLPanel status;
	
	@UiField
	HTMLPanel rootPanel;
	
	@UiField
	Modal modal;
	
	private Account receiver;
	private Account sender;

	public SendMessageWidget(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus, Account sender, Account receiver) {
		super(dispatcher, eventBus);
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		modal.show();
	}

	@Override
	protected void setupUiElements() {
	}
	
	@UiHandler("sendBtn")
	void send(ClickEvent event) {
		// validate form
		Message msg = new Message();
		msg.setSubject(SafeHtmlUtils.htmlEscape(subject.getText().trim()));
		msg.setMessage(SafeHtmlUtils.htmlEscape(message.getText().trim()));
//		service.sendMessage(sender, receiver, msg, new AsyncCallback<Void>() {
//			@Override
//			public void onSuccess(Void result) {
//				Alert alert = new Alert("Message sent");
//				alert.setType(AlertType.SUCCESS);
//				status.add(alert);
//				modal.hide();
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				Alert alert = new Alert("Message couldn't sent");
//				alert.setAnimation(true);
//				alert.setType(AlertType.ERROR);
//				status.add(alert);
//			}
//		});
	}

	@UiHandler("closeBtn")
	void close(ClickEvent event) {
		modal.hide();
	}
}
