package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.SimplePager;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.ListDataProvider;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.resource.TableResources;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.VPanel;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class ConversationView extends Composite implements IConversationView {

	public interface ConversationViewPresenter extends Presenter {
		void sendMessage(ConversationDTO conversation);

		void onView(ConversationDTO conversation);

		void gotoProfile();

		void getConversations(ConversationType type, int start, int pageSize);
	}

	private static final String SENDER_KEY = "Sender";
	private static final String SUBJECT_KEY = "Subject";
	private static final String TIME_RECEIVED_KEY = "Time received";
	private static final String RECEIVER_KEY = "Receiver";
	private static final int PAGE_SIZE = 10;
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
		
		String conversationRow();

		String replyTextArea();
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
	Anchor allMessagesLink;
	
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
	ListDataProvider<ConversationDTO> dataProvider;
	SimplePager pager;
	
	TableResources tableResources;
	boolean filterConversation;
	List<ConversationDTO> filteredConversations = new ArrayList<ConversationDTO>();
	Formatter<AccountDTO> accountFormatter;
	Formatter<Object> basicDataFormatter;
	protected int pageStart;
	private ConversationType type;
	
	@SuppressWarnings("unchecked")
	public ConversationView() {
		tableResources = GWT.create(TableResources.class);
		tableResources.cellTableStyle().ensureInjected();
		conversationTable = new CellTable<ConversationDTO>(PAGE_SIZE, tableResources);
		conversationTable.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(conversationTable);
		initWidget(uiBinder.createAndBindUi(this));
		
		buildConversationTable();
		
		inboxLinks.put(INBOXLINK.INBOX, messagesLink);
		inboxLinks.put(INBOXLINK.RECEIVED_MESSAGES, receivedMessagesLink);
		inboxLinks.put(INBOXLINK.SENT_MESSAGES, sentMessagesLink);
		setInboxLink(INBOXLINK.INBOX);
		
		accountFormatter = (Formatter<AccountDTO>) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION);
		basicDataFormatter = (Formatter<Object>) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);

		addConversationTableToPanel();
		dataProvider = new ListDataProvider<ConversationDTO>();
		dataProvider.addDataDisplay(conversationTable);
		pageStart = 0;
		type = ConversationType.ALL;
	}

	private void addConversationTableToPanel() {
		conversationPanel.clear();
		conversationPanel.add(conversationTable);
		conversationPanel.add(pager);
	}

	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
		if (conversations != null) {
			this.conversations = conversations;
			StyleHelper.show(pager.getElement(), true);
			if (conversations.size() == 0) {
				displayNoConversationMessage();
				return;
			}
			dataProvider.setList(conversations);
			setMessageCount(conversations);
		}
	}

	private void displayNoConversationMessage() {
		conversationPanel.add(new HTMLPanel("<p class='large_text'>"+StringConstants.NO_MESSAGES+"</p>"));
		StyleHelper.show(pager.getElement(), false);
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
	public void clear() {
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

	private Header<String> buildHeader(final String title) {
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
					pageStart = pager.getPageStart();
					displayConversation(event.getValue());
				}
			}
		});
	}
	
	private String getImageLink(AccountDTO acct) {
		return accountFormatter.format(acct, ValueType.SMALL_IMAGE_VALUE);
	}
	
	private Panel getImagePanel(final AccountDTO acct, ValueType imageValueType) {
		VPanel panel = new VPanel();
		Anchor anchor = new Anchor();
		anchor.setHTML(accountFormatter.format(acct, ValueType.MEDIUM_IMAGE_ONLY));
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(acct.getAccountId()));
			}
		});
		panel.add(anchor);
		panel.add(new HTMLPanel("<span>"+accountFormatter.format(acct, ValueType.NAME_VALUE)+"</span>"));
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

	Heading getHeading(int size, String text) {
		Heading heading = new Heading(size);
		heading.setText(text);
		return heading;
	}
	
	/**
	 * Displays the entire conversation
	 * @param conversation
	 */
	@Override
	public void displayConversation(final ConversationDTO conversation) {
		if (conversation != null) {
			presenter.onView(conversation);
			conversationPanel.clear();
			messagesPanel = new HTMLPanel("");
			
			FluidRow row = new FluidRow();
			row.addStyleName(style.conversationRow());
			com.github.gwtbootstrap.client.ui.Column col1 = new com.github.gwtbootstrap.client.ui.Column(1);
			col1.add(getHeading(4, SENDER_KEY));
			row.add(col1);
			
			com.github.gwtbootstrap.client.ui.Column col2 = new com.github.gwtbootstrap.client.ui.Column(5);
			col2.setOffset(1);
			col2.add(getImagePanel(conversation.getSender(), ValueType.SMALL_IMAGE_VALUE));
			row.add(col2);
			conversationPanel.add(row);
			
			row = new FluidRow();
			row.addStyleName(style.conversationRow());
			col1 = new com.github.gwtbootstrap.client.ui.Column(1);
			col1.add(getHeading(4, RECEIVER_KEY));
			row.add(col1);
			
			col2 = new com.github.gwtbootstrap.client.ui.Column(5);
			col2.setOffset(1);
			col2.add(getImagePanel(conversation.getReceiver(), ValueType.SMALL_IMAGE_VALUE));
			row.add(col2);
			conversationPanel.add(row);
			
			row = new FluidRow();
			row.addStyleName(style.conversationRow());
			col1 = new com.github.gwtbootstrap.client.ui.Column(1);
			col1.add(getHeading(4, SUBJECT_KEY));
			row.add(col1);
			
			col2 = new com.github.gwtbootstrap.client.ui.Column(5);
			col2.setOffset(1);
			col2.add(new HTMLPanel(basicDataFormatter.format(conversation.getSubject(), ValueType.STRING_VALUE)));
			row.add(col2);
			conversationPanel.add(row);
			conversationPanel.add(new HTMLPanel("<hr/>"));
			
			for (MessageDTO msg : conversation.getMessages()) {
				messagesPanel.add(displayMessage(msg));
				messagesPanel.add(new HTMLPanel("<hr/>"));
			}
			conversationPanel.add(messagesPanel);
			addReplyPanel(conversationPanel, conversation);
		}
	}

	/**
	 * Displays individual messages of a conversation
	 * @param msg
	 * @return
	 */
	private Panel displayMessage(final MessageDTO msg) {
		FluidContainer container = new FluidContainer();
		Row row = new Row();
		com.github.gwtbootstrap.client.ui.Column imageCol = new com.github.gwtbootstrap.client.ui.Column(3);
		imageCol.setOffset(1);
		imageCol.add(getImagePanel(msg.getSender(), ValueType.MEDIUM_IMAGE_VALUE));
		row.add(imageCol);
		
		com.github.gwtbootstrap.client.ui.Column messageCol = new com.github.gwtbootstrap.client.ui.Column(8);
		HTMLPanel messagePanel = new HTMLPanel("<span class='medium_text'>" + msg.getMessage() + "</span>");
		messageCol.add(messagePanel);
		row.add(messageCol);
		container.add(row);
		
		Row timeRow = new Row();
		com.github.gwtbootstrap.client.ui.Column timeCol = new com.github.gwtbootstrap.client.ui.Column(4);
		timeCol.setOffset(8);
		HTMLPanel timePanel = new HTMLPanel("<span class='tiny_text'>sent on "
				+basicDataFormatter.format(msg.getTimeCreated(), ValueType.DATE_VALUE_SHORT)+"</span>");
		timeCol.add(timePanel);
		timeRow.add(timeCol);
		container.add(timeRow);
		return container;
	}

	/**
	 * Adds a reply panel
	 * @param root
	 * @param c
	 */
	private void addReplyPanel(final HTMLPanel root, final ConversationDTO c) {
		FluidContainer container = new FluidContainer();

		// alert row
		Row row = new Row();
		com.github.gwtbootstrap.client.ui.Column col = new com.github.gwtbootstrap.client.ui.Column(10);
		final Alert info = new Alert();
		info.setVisible(false);
		col.add(info);
		row.add(col);
		container.add(row);
		
		// TextArea row
		row = new Row();
		com.github.gwtbootstrap.client.ui.Column replyTextCol = new com.github.gwtbootstrap.client.ui.Column(10);

		replyTextCol.setOffset(1);
		HTMLPanel replyPanel = new HTMLPanel("");

		final ControlGroup replyTextAreaCg = new ControlGroup();
		final TextArea replyTextArea = new TextArea();
		replyTextArea.addStyleName(style.replyTextArea());
		final HelpInline replyTextAreaHelpInline = new HelpInline();
		replyTextAreaHelpInline.setStyleName(style.helpinline());
		replyTextAreaCg.add(replyTextArea);

		replyTextAreaCg.add(replyTextAreaHelpInline);
		replyPanel.add(replyTextAreaCg);
		replyTextCol.add(replyPanel);
		row.add(replyTextCol);
		container.add(row);
		
		// buttons
		Row buttonRow = new Row();
		com.github.gwtbootstrap.client.ui.Column replyBtnCol = new com.github.gwtbootstrap.client.ui.Column(1);
		replyBtnCol.setOffset(1);
		buttonRow.add(replyBtnCol);
		
		Button replyBtn = new Button("Reply");
		replyBtn.setType(ButtonType.PRIMARY);
		replyBtnCol.add(replyBtn);
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

		com.github.gwtbootstrap.client.ui.Column backBtnCol = new com.github.gwtbootstrap.client.ui.Column(2);
		buttonRow.add(backBtnCol);
	
		Button backBtn = new Button("Back");
		backBtn.addStyleName(style.button());
		backBtnCol.add(backBtn);
		backBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getConversations(type, pageStart);
				addConversationTableToPanel();
			}
		});
		container.add(buttonRow);
		root.add(container);
	}
	
	@UiHandler("profileLink")
	void onProfileLinkClick(ClickEvent event) {
		presenter.gotoProfile();
	}
	
	@UiHandler("settingsLink")
	void onSettingsLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountSettingsPlace());
	}
	
	@UiHandler({"allMessagesLink", "messagesLink"})
	void onInboxLinkClicked(ClickEvent event) {
		pageStart = 0;
		type = ConversationType.ALL;
		getConversations(type, pageStart);
		addConversationTableToPanel();
	}
	
	@UiHandler("receivedMessagesLink")
	void displayRecievedMessages(ClickEvent event) {
		pageStart = 0;
		type = ConversationType.RECEIVED;
		getConversations(type, pageStart);
		addConversationTableToPanel();
	}
	
	@UiHandler("sentMessagesLink")
	void displaySentMessages(ClickEvent event) {
		pageStart = 0;
		type = ConversationType.SENT;
		getConversations(type, pageStart);
		addConversationTableToPanel();
	}

	private void getConversations(ConversationType type, int pageStart) {
		presenter.getConversations(type, pageStart, 0);
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

	@Override
	public int getConversationPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void setTotalConversation(Long totalConversations) {
		conversationTable.setRowCount(totalConversations.intValue(), true);
	}
}
