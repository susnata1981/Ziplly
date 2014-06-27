package com.ziplly.app.client.conversation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.MessageDTO;

public class MessageDetailsWidget extends Composite {

  private static MessageDetailsWidgetUiBinder uiBinder = GWT
      .create(MessageDetailsWidgetUiBinder.class);

  interface MessageDetailsWidgetUiBinder extends UiBinder<Widget, MessageDetailsWidget> {
  }

  public MessageDetailsWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiField
  Image senderImage;
  @UiField
  SpanElement senderName;
  @UiField
  SpanElement messageSpan;
  @UiField
  SpanElement timeSent;
  
  private BasicDataFormatter formatter = new BasicDataFormatter();
  
  public void displayMessage(MessageDTO message) {
    if (message == null) {
      return;
    }
    
    senderImage.setUrl(formatter.format(message.getSender(), ValueType.PROFILE_IMAGE_URL));
    senderName.setInnerText(message.getSender().getDisplayName());
    messageSpan.setInnerHTML(message.getMessage());
    timeSent.setInnerText(formatter.format(message.getTimeCreated(), ValueType.DATE_DIFF));
  }

}
