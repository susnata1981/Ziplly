package com.ziplly.app.client.widget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.conversation.ConversationView.ConversationViewPresenter;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.factory.AbstractValueFormatter;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AccountFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class ConversationWidget extends Composite implements View<ConversationViewPresenter> {

	private static ConversationWidgetUiBinder uiBinder = GWT.create(ConversationWidgetUiBinder.class);

	interface ConversationWidgetUiBinder extends UiBinder<Widget, ConversationWidget> {
	}

	@UiField
	HTMLPanel conversationPanel;
	private ConversationViewPresenter presenter;
	private Map<ConversationDTO, HTMLPanel> conversationToPanelMap =
	    new HashMap<ConversationDTO, HTMLPanel>();
	private Map<ConversationDTO, HTMLPanel> conversationToReplyPanelMap =
	    new HashMap<ConversationDTO, HTMLPanel>();
	private AccountFormatter accountFormatter = (AccountFormatter) AbstractValueFormatterFactory
      .getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION); 
			
	@Inject
	public ConversationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(ConversationViewPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		conversationPanel.clear();
	}

	public void updateConversation(final ConversationDTO c) {
		HTMLPanel panel = conversationToPanelMap.get(c);
		if (panel == null) {
			HTMLPanel cPanel = new HTMLPanel("<hr/>");
			conversationToPanelMap.put(c, cPanel);
			displayConversation(cPanel, c);
			HTMLPanel replyPanel = conversationToReplyPanelMap.get(c);
			if (replyPanel == null) {
				// error
				throw new RuntimeException("Couldn't find replypanel for conversation: " + c.getId());
			}
			TextBox tbox = (TextBox) replyPanel.getWidget(0);
			tbox.setText("");
		} else {
			int size = c.getMessages().size();
			displayMessage(panel, c.getMessages().get(size - 1), c);
		}
	}

	public void displayConversations(List<ConversationDTO> conversations) {
		conversationPanel.clear();
		for (final ConversationDTO c : conversations) {
			HTMLPanel panel = new HTMLPanel("<hr/>");
			conversationToPanelMap.put(c, panel);
			displayConversation(panel, c);
			conversationPanel.add(panel);
			HTMLPanel replyPanel = new HTMLPanel("");
			conversationToReplyPanelMap.put(c, replyPanel);
			addReplyPanel(replyPanel, c);
			conversationPanel.add(replyPanel);
			conversationPanel.getElement().getStyle().setBackgroundColor("#e9e9e8");
			conversationPanel.getElement().getStyle().setMargin(10, Unit.PX);
			conversationPanel.getElement().getStyle().setBorderWidth(2, Unit.PX);
		}
	}

	void displayConversation(final HTMLPanel panel, final ConversationDTO c) {
		for (final MessageDTO message : c.getMessages()) {
			displayMessage(panel, message, c);
		}
	}

	private void addReplyPanel(final HTMLPanel panel, final ConversationDTO c) {
		HTMLPanel replyPanel = new HTMLPanel("");
		final TextBox replyTextBox = new TextBox();
		replyTextBox.setWidth("400px");
		replyTextBox.setHeight("40px");
		Button replyBtn = new Button("reply");
		replyBtn.setType(ButtonType.PRIMARY);
		replyPanel.add(replyTextBox);
		replyPanel.add(replyBtn);
		panel.add(replyPanel);
		replyBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ConversationDTO conversation = new ConversationDTO();
				conversation.setId(c.getId());
				conversation.setSender(c.getSender());
				conversation.setReceiver(c.getReceiver());
				conversation.setSubject(c.getSubject());
				conversation.setTimeUpdated(new Date());
				conversation.getMessages().addAll(c.getMessages());
				MessageDTO m = new MessageDTO();
				m.setMessage(replyTextBox.getText());
				m.setTimeCreated(new Date());
				if (c.isSender()) {
					m.setReceiver(c.getReceiver());
					m.setSender(c.getSender());
				} else {
					m.setReceiver(c.getSender());
					m.setSender(c.getReceiver());
				}
				conversation.add(m);
				presenter.sendMessage(conversation);
			}
		});
	}

	private void displayMessage(final HTMLPanel panel,
	    final MessageDTO message,
	    final ConversationDTO c) {
		HTMLPanel senderDiv = new HTMLPanel("");
		Anchor profileLink = new Anchor(message.getSender().getDisplayName());
		profileLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (message.getSender() instanceof PersonalAccountDTO) {
					presenter.goTo(new PersonalAccountPlace(message.getSender().getAccountId()));
				}
			}
		});

		Image profileImage = new Image(accountFormatter.format(message.getSender(), ValueType.PROFILE_IMAGE_URL));
		profileImage.setWidth("60px");
		profileImage.setHeight("50px");

		senderDiv.add(profileLink);
		senderDiv.add(profileImage);
		panel.add(senderDiv);

		HTMLPanel content =
		    new HTMLPanel("<p>Subject:" + c.getSubject() + "</p><p>Message:" + message.getMessage()
		        + "</p>");
		panel.add(content);
	}

}
