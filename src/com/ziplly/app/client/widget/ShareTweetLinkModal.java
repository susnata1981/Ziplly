package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ShareTweetLinkModal extends Composite {

	private static ShareTweetLinkModalUiBinder uiBinder = GWT
	    .create(ShareTweetLinkModalUiBinder.class);

	interface ShareTweetLinkModalUiBinder extends UiBinder<Widget, ShareTweetLinkModal> {
	}

	@UiField
	Modal shareTweetLinkModal;

	@UiField
	TextBox shareTextBox;

	@UiField
	Button gotoTweetBtn;

	public ShareTweetLinkModal() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setTweetLink(String link) {
		shareTextBox.setText(link);
	}

	public Button getGotoTweetButton() {
		return gotoTweetBtn;
	}

	public void show() {
		shareTweetLinkModal.show();
	}

	public void hide() {
		shareTweetLinkModal.hide();
	}

	@UiHandler("closeBtn")
	public void close(ClickEvent event) {
		shareTweetLinkModal.hide();
	}
}
