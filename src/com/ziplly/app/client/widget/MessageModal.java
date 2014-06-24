package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MessageModal extends Composite {
  private static MessageModalUiBinder uiBinder = GWT.create(MessageModalUiBinder.class);

  interface MessageModalUiBinder extends UiBinder<Widget, MessageModal> {
  }

  public MessageModal() {
    initWidget(uiBinder.createAndBindUi(this));
    modal.setKeyboard(false);
  }

  @UiField
  Element title;
  @UiField
  Modal modal;
  @UiField
  HTMLPanel modalContainer;
  @UiField
  Element content;
  @UiField
  Button okBtn;

  public void setWidget(Widget widget) {
    modalContainer.add(widget);
    StyleHelper.show(content, false);
  }

  public void setContent(String msg) {
    content.setInnerHTML(msg);
  }

  public void setTitle(String msg) {
    title.setInnerText(msg);

  }

  @UiHandler("okBtn")
  public void ok(ClickEvent event) {
    modal.hide();
  }

  public Button getButton() {
    return okBtn;
  }

  public void show() {
    modal.show();
  }

  public void hide() {
    modal.hide();
  }
}
