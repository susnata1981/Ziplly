package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class ConversationView extends Composite implements IConversationView {

	public interface ConversationViewPresenter extends Presenter {
		void getConversations();

		void sendMessage(ConversationDTO conversation);

		void onView(ConversationDTO conversation);
	}

	private static ConversationViewUiBinder uiBinder = GWT
			.create(ConversationViewUiBinder.class);

	interface ConversationViewUiBinder extends
			UiBinder<Widget, ConversationView> {
	}

	public interface Style extends CssResource {
		String conversation();

		String subjectHeader();

		String senderBlock();

		String subjectBlock();

		String dateBlock();

		String replyPanel();

		String buttonPanel();
		
		String button();

		String conversationNotRead();
	}

	@UiField
	Style style;

	@UiField
	HTMLPanel conversationPanel;

	HTMLPanel messagesPanel;

	@UiField
	SpanElement unreadMessageCount;
	
	@UiField
	Anchor profileLink;
	
	@UiField
	Anchor settingsLink;
	
	ConversationViewPresenter presenter;

	public ConversationView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void clear() {
		conversationPanel.clear();
	}

	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
		if (conversations != null) {
			conversationPanel.clear();
			int unreadMsgCount = 0;
			for (ConversationDTO conversation : conversations) {
				conversationPanel.add(buildConversationPanel(conversation));
				if (isConversationUnread(conversation)) {
					unreadMsgCount++;
				}
			}
			if (unreadMsgCount > 0) {
				unreadMessageCount.setInnerHTML("("+unreadMsgCount+")");
			}
		}
	}

	private boolean isConversationUnread(ConversationDTO conversation) {
		return conversation.getStatus()==ConversationStatus.UNREAD && !conversation.isSender();
	}

	/*
	 * Displays conversation summary
	 */
	private HTMLPanel buildConversationPanel(final ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}
		HTMLPanel panel = new HTMLPanel("");
		Anchor senderLink = new Anchor();
		senderLink.setText(conversation.getSender().getDisplayName());
		senderLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(conversation
						.getSender().getAccountId()));
			}
		});
		senderLink.addStyleName(style.senderBlock());
		panel.add(senderLink);

		String subjectLine = conversation.getSubject();
		
		if (isConversationUnread(conversation)) {
			subjectLine += "&nbsp;(unread)";
			panel.getElement().getStyle().setBackgroundColor("#D8DFE6");
		}
		
		HTMLPanel messagePanel = new HTMLPanel("<a><span class='medium_text'>"
				+ subjectLine + "</span></a>");
		messagePanel.addStyleName(style.subjectBlock());
		panel.add(messagePanel);

		String date = DateTimeFormat.getFormat(
				PredefinedFormat.DATE_TIME_MEDIUM).format(
				conversation.getMessages().get(0).getTimeCreated());

		HTMLPanel timePanel = new HTMLPanel("<span class='medium_text'>" + date
				+ "</span>");
		timePanel.addStyleName(style.dateBlock());
		panel.add(timePanel);

		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayConversation(conversation);
			}
		};

		panel.addDomHandler(handler, ClickEvent.getType());
		panel.setStyleName(style.conversation());
		return panel;
	}

	@Override
	public void setPresenter(ConversationViewPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void updateConversation(ConversationDTO c) {
		int size = c.getMessages().size();
		MessageDTO latestMessage = c.getMessages().get(size - 1);
		if (conversationPanel != null) {
			messagesPanel.add(displayMessage(latestMessage));
		}
	}

	/*
	 * Displays the entire conversation
	 */
	@Override
	public void displayConversation(final ConversationDTO conversation) {
		if (conversation != null) {
			presenter.onView(conversation);
			conversationPanel.clear();
			messagesPanel = new HTMLPanel("");
			HTMLPanel subjectPanel = new HTMLPanel("<span>Subject:&nbsp;"
					+ conversation.getSubject() + "</span>");
			subjectPanel.setStyleName(style.subjectHeader());
			conversationPanel.add(subjectPanel);

			for (MessageDTO msg : conversation.getMessages()) {
				messagesPanel.add(displayMessage(msg));
			}
			conversationPanel.add(messagesPanel);
			addReplyPanel(conversationPanel, conversation);
		}
	}

	private HTMLPanel displayMessage(final MessageDTO msg) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(style.conversation());
		Anchor senderLink = new Anchor();
		senderLink.setText(msg.getSender().getDisplayName());
		senderLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(msg.getSender()
						.getAccountId()));
			}
		});
		senderLink.setStyleName(style.senderBlock());
		panel.add(senderLink);

		HTMLPanel messagePanel = new HTMLPanel("<span class='medium_text'>"
				+ msg.getMessage() + "</span>");
		messagePanel.setStyleName(style.subjectBlock());
		panel.add(messagePanel);

		String date = DateTimeFormat.getFormat(
				PredefinedFormat.DATE_TIME_MEDIUM).format(msg.getTimeCreated());
		HTMLPanel timePanel = new HTMLPanel("<span class='medium_text'>" + date
				+ "</span>");
		timePanel.setStyleName(style.dateBlock());
		panel.add(timePanel);
		return panel;
	}

	private void addReplyPanel(final HTMLPanel root, final ConversationDTO c) {
		HTMLPanel replyPanel = new HTMLPanel("");
		replyPanel.addStyleName(style.replyPanel());

		final Alert info = new Alert();
		info.setVisible(false);
		replyPanel.add(info);

		final TextArea replyTextArea = new TextArea();
		replyPanel.add(replyTextArea);

		// buttons
		HTMLPanel buttonPanel = new HTMLPanel("");
		buttonPanel.addStyleName(style.buttonPanel());

		Button replyBtn = new Button("Reply");
		replyBtn.addStyleName(style.button());
		replyBtn.setType(ButtonType.PRIMARY);
		replyBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				info.setVisible(false);
				ValidationResult result = FieldVerifier
						.validateTweet(replyTextArea.getText());
				if (!result.isValid()) {
					info.setText(result.getErrors().get(0).getErrorMessage());
					info.setType(AlertType.ERROR);
					info.setVisible(true);
					return;
				}

				ConversationDTO conversation = new ConversationDTO();
				conversation.setId(c.getId());
				conversation.setSender(c.getSender());
				conversation.setReceiver(c.getReceiver());
				conversation.setSubject(c.getSubject());
				conversation.setTimeUpdated(new Date());
				conversation.getMessages().addAll(c.getMessages());
				MessageDTO m = new MessageDTO();
				m.setMessage(replyTextArea.getText());
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
				replyTextArea.setText("");
			}
		});
		buttonPanel.add(replyBtn);

		Button backBtn = new Button("Back");
		backBtn.addStyleName(style.button());
		backBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new ConversationPlace());
			}
		});
		buttonPanel.add(backBtn);
		replyPanel.add(buttonPanel);
		root.add(replyPanel);
	}
	
	@UiHandler("profileLink")
	void onProfileLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountPlace());
	}
	
	@UiHandler("settingsLink")
	void onSettingsLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountSettingsPlace());
	}
}
