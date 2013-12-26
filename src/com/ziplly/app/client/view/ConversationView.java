package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.resource.TableResources;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.AccountDTO;
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

		void gotoProfile();
	}

	private static final String SENDER_KEY = "Sender";
	private static final String SUBJECT_KEY = "Subject";
	private static final String TIME_RECEIVED_KEY = "Time received";
	private static final String RECEIVER_KEY = "Receiver";
	private static final int MAX_ROW_COUNT = 10;
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
		
		String subjectFont();
		
		String helpinline();
	}

	@UiField
	Style style;

	@UiField
	HTMLPanel conversationPanel;

	HTMLPanel messagesPanel;

	@UiField
	SpanElement unreadMessageCountSpan;
	@UiField
	SpanElement sentMessageCountSpan;
	@UiField
	SpanElement receivedMessageCountSpan;
	
	@UiField
	Anchor receivedMessagesLink;
	
	@UiField
	Anchor sentMessagesLink;
	
	@UiField
	Anchor messagesLink;
	
	@UiField
	Anchor profileLink;
	
	@UiField
	Anchor settingsLink;
	
	ConversationViewPresenter presenter;

	Map<INBOXLINK, Anchor> inboxLinks = new HashMap<INBOXLINK, Anchor>();
	List<ConversationDTO> conversations;
	CellTable<ConversationDTO> conversationTable;
	TableResources tableResources;
	boolean filterConversation;
	List<ConversationDTO> filteredConversations = new ArrayList<ConversationDTO>();
	Formatter<AccountDTO> accountFormatter;
	Formatter<Object> basicDataFormatter;
	
	@SuppressWarnings("unchecked")
	public ConversationView() {
		tableResources = GWT.create(TableResources.class);
		tableResources.cellTableStyle().ensureInjected();
		conversationTable = new CellTable<ConversationDTO>(MAX_ROW_COUNT, tableResources);
		initWidget(uiBinder.createAndBindUi(this));
		buildConversationTable();
		inboxLinks.put(INBOXLINK.INBOX, messagesLink);
		inboxLinks.put(INBOXLINK.RECEIVED_MESSAGES, receivedMessagesLink);
		inboxLinks.put(INBOXLINK.SENT_MESSAGES, sentMessagesLink);
		setInboxLink(INBOXLINK.INBOX);
		accountFormatter = (Formatter<AccountDTO>) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION);
		basicDataFormatter = (Formatter<Object>) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
	}

	@Override
	public void clear() {
		conversationPanel.clear();
	}

	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
		if (conversations != null) {
			this.conversations = conversations;
			conversationPanel.add(conversationTable);
			internalDisplayConversations();
			setMessageCount(conversations);
		}
	}

	private void internalDisplayConversations() {
		conversationPanel.clear();
		List<ConversationDTO> convstns;
		if (filterConversation) {
			convstns = filteredConversations;
		} else {
			convstns = conversations;
		}
		
		if (convstns.size() == 0) {
			displayNoConversationMessage();
		}
		
		conversationTable.setRowData(convstns);
		conversationPanel.add(conversationTable);
	}

	private void displayNoConversationMessage() {
		conversationPanel.add(new HTMLPanel("<p class='large_text'>"+StringConstants.NO_MESSAGES+"</p>"));
	}

	@Override
	public void setMessageCount(List<ConversationDTO> conversations) {
		int unreadMsgCount = 0,  receivedMessageCount = 0, sentMessageCount = 0;
		
		for (ConversationDTO conversation : conversations) {
			if (isConversationUnread(conversation)) {
				unreadMsgCount++;
			}
			if (conversation.isSender()) {
				sentMessageCount++;
			} else {
				receivedMessageCount++;
			}
		}
		updateCount(unreadMessageCountSpan, unreadMsgCount, false);
//		updateCount(receivedMessageCountSpan, receivedMessageCount, true);
//		updateCount(sentMessageCountSpan, sentMessageCount, true);
	}
	
	@Override
	public void clearMessageCount() {
		
	}

	void updateCount(SpanElement elem, int value, boolean setZero) {
		if (value > 0) {
			elem.setInnerHTML("("+value+")");
		} else if (setZero) {
			elem.setInnerHTML("(0)");
		} else if (!setZero) {
			elem.setInnerHTML("");
		}
	}
	
	private boolean isConversationUnread(ConversationDTO conversation) {
		return conversation.getStatus()==ConversationStatus.UNREAD && !conversation.isSender();
	}

	/*
	 * Displays conversation summary
	 *
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
	*/
	
	Header<String> buildHeader(final String title) {
		Header<String> header = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return title;
			}
		};
		return header;
	}
	
	void buildConversationTable() {
		conversationTable.setStyleName(style.conversation());
		Column<ConversationDTO, String> senderColumn = new Column<ConversationDTO, String>(new ClickableTextCell() {
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				ConversationDTO c;
				if (filterConversation) {
					c = filteredConversations.get(context.getIndex());
				} else {
					c = conversations.get(context.getIndex());
				}
				sb.appendHtmlConstant(getImageLink(c.getSender()));
			}
			}) {
				@Override
				public String getValue(ConversationDTO c) {
					return c.getSender().getImageUrl();
				}
		};
		conversationTable.addColumn(senderColumn, buildHeader(SENDER_KEY));
		conversationTable.setColumnWidth(senderColumn, 20, Unit.PCT);
		
		Column<ConversationDTO, String> receiverColumn = new Column<ConversationDTO, String>(new ClickableTextCell() {
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				ConversationDTO c;
				if (filterConversation) {
					c = filteredConversations.get(context.getIndex());
				} else {
					c = conversations.get(context.getIndex());
				}
				sb.appendHtmlConstant(getImageLink(c.getReceiver()));
			}
			}) {
				@Override
				public String getValue(ConversationDTO c) {
					return c.getSender().getImageUrl();
				}
		};
		conversationTable.addColumn(receiverColumn, buildHeader(RECEIVER_KEY));
		conversationTable.setColumnWidth(receiverColumn, 20, Unit.PCT);
		
		Column<ConversationDTO, String> subjectCol = new TextColumn<ConversationDTO>() {
			@Override
			public String getValue(ConversationDTO c) {
				return c.getSubject();
			}
		};
//		subjectCol.setCellStyleNames(style.subjectBlock());
		subjectCol.setCellStyleNames(style.subjectFont());
		conversationTable.addColumn(subjectCol, buildHeader(SUBJECT_KEY));
		conversationTable.setColumnWidth(subjectCol, 30, Unit.PCT);
		
		Column<ConversationDTO, String> timeSentCol = new TextColumn<ConversationDTO>() {
			@Override
			public String getValue(ConversationDTO c) {
				return basicDataFormatter.format(c.getMessages().get(0).getTimeCreated(), ValueType.DATE_VALUE_SHORT);
			}
		};
//		timeSentCol.setCellStyleNames(style.dateBlock());
		conversationTable.addColumn(timeSentCol, buildHeader(TIME_RECEIVED_KEY));
		conversationTable.setColumnWidth(timeSentCol, 30, Unit.PCT);
		
		conversationTable.addCellPreviewHandler(new Handler<ConversationDTO>(){

			@Override
			public void onCellPreview(CellPreviewEvent<ConversationDTO> event) {
				boolean isClick = "click".equals(event.getNativeEvent().getType());
				if (isClick) {
					displayConversation(event.getValue());
				}
			}
		});
	}
	
	private String getImageLink(AccountDTO acct) {
		return accountFormatter.format(acct, ValueType.SMALL_IMAGE_VALUE);
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
			
			StringBuilder temp = new StringBuilder();
			temp.append("From:"+getImageLink(conversation.getSender()));
			temp.append("<br>To:"+getImageLink(conversation.getReceiver()));
			temp.append("<br><br><span>Subject:&nbsp;"+ conversation.getSubject() + "</span>");
			
			HTMLPanel subjectPanel = new HTMLPanel(temp.toString());
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
		StringBuilder temp = new StringBuilder();
		temp.append("<img width='20' height='20' src='"+msg.getSender().getImageUrl()+"'/>");
		temp.append("&nbsp;"+msg.getSender().getDisplayName());
		senderLink.setHTML(temp.toString());
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
		final ControlGroup replyTextAreaCg = new ControlGroup();
		final TextArea replyTextArea = new TextArea();
		final HelpInline replyTextAreaHelpInline = new HelpInline();
		replyTextAreaHelpInline.setStyleName(style.helpinline());
		replyTextAreaCg.add(replyTextArea);
		replyTextAreaCg.add(replyTextAreaHelpInline);
		replyPanel.add(replyTextAreaCg);

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
					replyTextAreaCg.setType(ControlGroupType.ERROR);
					replyTextAreaHelpInline.setText(result.getErrors().get(0).getErrorMessage());
					replyTextAreaHelpInline.setVisible(true);
					return;
				}

				replyTextAreaCg.setType(ControlGroupType.NONE);
				replyTextAreaHelpInline.setVisible(false);		
				
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
		presenter.gotoProfile();
	}
	
	@UiHandler("settingsLink")
	void onSettingsLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountSettingsPlace());
	}
	
	@UiHandler("messagesLink")
	void onInboxLinkClicked(ClickEvent event) {
		filterConversation = false;
		setInboxLink(INBOXLINK.INBOX);
		internalDisplayConversations();
	}
	
	@UiHandler("receivedMessagesLink")
	void displayRecievedMessages(ClickEvent event) {
		filteredConversations.clear();
		setInboxLink(INBOXLINK.RECEIVED_MESSAGES);
		for(ConversationDTO c : conversations) {
			if (!c.isSender()) {
				filteredConversations.add(c);
			}
		}
		filterConversation = true;
		internalDisplayConversations();
	}
	
	@UiHandler("sentMessagesLink")
	void displaySentMessages(ClickEvent event) {
		filteredConversations.clear();
		setInboxLink(INBOXLINK.SENT_MESSAGES);
		for(ConversationDTO c : conversations) {
			if (c.isSender()) {
				filteredConversations.add(c);
			}
		}
		filterConversation = true;
		internalDisplayConversations();
	}

	private void setInboxLink(INBOXLINK link) {
		for(INBOXLINK l : INBOXLINK.values()) {
			if (l == link) {
				inboxLinks.get(l).getElement().getParentElement().getStyle().setBackgroundColor("#ddd");
			} else {
				inboxLinks.get(l).getElement().getParentElement().getStyle().clearBackgroundColor();
			}
		}
	}
	
	public static enum INBOXLINK {
		INBOX,
		RECEIVED_MESSAGES,
		SENT_MESSAGES
	}
}
