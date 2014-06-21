package com.ziplly.app.client.conversation;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class ConversationDetailsView extends Composite {

  private static ConversationDetailsViewUiBinder uiBinder = GWT
      .create(ConversationDetailsViewUiBinder.class);

  interface ConversationDetailsViewUiBinder extends UiBinder<Widget, ConversationDetailsView> {
  }

  public ConversationDetailsView() {
    initWidget(uiBinder.createAndBindUi(this));
    replyHelpInline.setVisible(false);
  }
  
  @UiField
  SpanElement senderLabel;
  @UiField
  SpanElement receiverLabel;
  @UiField
  SpanElement timeLabel;
  @UiField
  SpanElement subjectLabel;
  @UiField
  HTMLPanel messageListPanel;
  @UiField
  ControlGroup replyCg;
  @UiField
  TextArea replyTextArea;
  @UiField
  HelpInline replyHelpInline;
  @UiField
  Button replyBtn;
  @UiField
  Button backBtn;
  
  private BasicDataFormatter formatter = new BasicDataFormatter();

  public void displayConversation(ConversationDTO conversation) {
    if (conversation == null) {
      return;
    }
    
    senderLabel.setInnerText(conversation.getSender().getDisplayName());
    timeLabel.setInnerText(formatter.format(conversation.getTimeCreated(), ValueType.DATE_VALUE_FULL));
    receiverLabel.setInnerText(conversation.getReceiver().getDisplayName());
    subjectLabel.setInnerText(conversation.getSubject());
    
    messageListPanel.clear();
    for(MessageDTO msg : conversation.getMessages()) {
      displayMessage(msg);
    }
  }

  public Button getReplyButton() {
    return replyBtn;
  }

  public boolean validateReplyContent() {
    clearError();
    ValidationResult result = FieldVerifier.validateMessage(replyTextArea.getText());
    if (!result.isValid()) {
      replyCg.setType(ControlGroupType.ERROR);
      replyHelpInline.setText(result.getErrors().get(0).getErrorMessage());
      replyHelpInline.setVisible(true);
      return false;
    }
    
    return true;
  }
  
  public void clearError() {
    replyHelpInline.setVisible(false);
    replyHelpInline.setText("");
    replyCg.setType(ControlGroupType.NONE);
  }
  
  public void clear() {
    replyTextArea.setText("");
  }
  
  private void displayMessage(final MessageDTO msg) {
    MessageDetailsWidget mWidget = new MessageDetailsWidget();
    mWidget.displayMessage(msg);
    messageListPanel.add(mWidget);
  }

  public String getReplyContent() {
    return FieldVerifier.getEscapedText(replyTextArea.getText());
  }

  public Button getBackButton() {
    return backBtn;
  }
}
