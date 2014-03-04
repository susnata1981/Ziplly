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
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.resource.TableResources;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.HPanel;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.ValidationResult;

public class ConversationView extends AbstractView implements IConversationView {

	public interface ConversationViewPresenter extends Presenter {
		void sendMessage(ConversationDTO conversation);

		void onView(ConversationDTO conversation);

		void getConversations(GetConversationsAction action);

		AccountDTO getAccount();
	}

	private static final String SENDER_KEY = "Sender";
	private static final String SUBJECT_KEY = "Subject";
	private static final String TIME_RECEIVED_KEY = "Time received";
	private static final String RECEIVER_KEY = "Receiver";
	private static final int PAGE_SIZE = 10;
	private static final String STATUS_COL_KEY = "Status";
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
		
		String message();
	}

	@UiField
	Style style;

	@UiField
	Row conversationRow;
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
	
	@UiField
	Alert message;
	
	ConversationViewPresenter presenter;

	Map<INBOXLINK, Anchor> inboxLinks = new HashMap<INBOXLINK, Anchor>();
	List<ConversationDTO> conversations;
	
	CellTable<ConversationDTO> conversationTable;
	SimplePager pager;
	
	TableResources tableResources;
	List<ConversationDTO> filteredConversations = new ArrayList<ConversationDTO>();
	private ConversationViewState state;

	@Inject
	public ConversationView(EventBus eventBus) {
		super(eventBus);
		tableResources = GWT.create(TableResources.class);
		tableResources.cellTableStyle().ensureInjected();
		conversationTable = new CellTable<ConversationDTO>(PAGE_SIZE, tableResources);
		conversationTable.setPageSize(PAGE_SIZE);
		conversationTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				state.onRangeChange(event.getNewRange());
				presenter.getConversations(state.getSearchCriteria());
			}
			
		});
			
		pager = new SimplePager();
		pager.setDisplay(conversationTable);
		initWidget(uiBinder.createAndBindUi(this));
		
		buildConversationTable();
		
		inboxLinks.put(INBOXLINK.INBOX, messagesLink);
		inboxLinks.put(INBOXLINK.RECEIVED_MESSAGES, receivedMessagesLink);
		inboxLinks.put(INBOXLINK.SENT_MESSAGES, sentMessagesLink);
		setInboxLink(INBOXLINK.INBOX);
		
		conversationPanel.add(pager);
		conversationPanel.add(conversationTable);
		state = new ConversationViewState(PAGE_SIZE);
		StyleHelper.show(message.getElement(), false);
		setBackgroundImage(ZResources.IMPL.profileBackground().getSafeUri().asString());
	}

	private void clearConversationTable() {
		conversationPanel.clear();
		conversationPanel.add(pager);
		conversationTable.setRowCount(0);
		conversationPanel.add(conversationTable);
	}

	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
		if (conversations != null) {
			this.conversations = conversations;
			StyleHelper.show(message.getElement(), false);
			StyleHelper.show(conversationRow.getElement(), true);
			StyleHelper.show(pager.getElement(), true);
			if (conversations.size() == 0) {
				displayNoConversationMessage();
				return;
			}
			conversationTable.setRowData(state.getStart(), conversations);
			highlightUnreadConversation();
		}
	}

	private void displayNoConversationMessage() {
		conversationPanel.add(message);
		StyleHelper.show(pager.getElement(), false);
		message.setText(StringConstants.NO_MESSAGES);
		message.setType(AlertType.INFO);
		StyleHelper.show(message.getElement(), true);
	}

	private void highlightUnreadConversation() {
		int start = state.getStart();
		int end = start + PAGE_SIZE;
		if (conversations == null) {
			return;
		}
		int totalConversations = conversations.size();
			
		for(int i=start; i<end; i++) {
			if (i >= totalConversations) {
				return;
			}
			ConversationDTO c = conversations.get(i);
			System.out.println("S="+c.isSender()+" S="+c.getStatus());
			if (/* !c.isSender() && */ c.getStatus() == ConversationStatus.UNREAD) {
				TableRowElement row = conversationTable.getRowElement(i-start);
				row.getStyle().setBackgroundColor("#e5e5e5");
			}
		}
	}
	
	@Override
	public void clear() {
		StyleHelper.show(conversationRow.getElement(), false);
		StyleHelper.clearBackground();
	}
	
	@Override
	public void clearMessageCount() {
		updateCount(unreadMessageCountSpan, 0, false);
	}

	@Override
	public void setUnreadMessageCount(int count) {
		updateCount(unreadMessageCountSpan, count, true);
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
				c = conversations.get(context.getIndex() - state.getStart());
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
				c = conversations.get(context.getIndex() - state.getStart());
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
		subjectCol.setCellStyleNames(style.subjectFont());
		conversationTable.addColumn(subjectCol, buildHeader(SUBJECT_KEY));
		conversationTable.setColumnWidth(subjectCol, 28, Unit.PCT);
		
		Column<ConversationDTO, String> statusCol = new TextColumn<ConversationDTO>() {

			@Override
			public String getValue(ConversationDTO c) {
				return c.getStatus().name().toLowerCase();
			}
		};
		conversationTable.addColumn(statusCol, buildHeader(STATUS_COL_KEY));
		conversationTable.setColumnWidth(statusCol, 6, Unit.PCT);
		
		Column<ConversationDTO, String> timeSentCol = new TextColumn<ConversationDTO>() {
			@Override
			public String getValue(ConversationDTO c) {
				return basicDataFormatter.format(c.getMessages().get(0).getTimeCreated(), ValueType.DATE_VALUE_MEDIUM);
			}
		};
		conversationTable.addColumn(timeSentCol, buildHeader(TIME_RECEIVED_KEY));
		conversationTable.setColumnWidth(timeSentCol, 26, Unit.PCT);
		
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
		return accountFormatter.format(acct, ValueType.TINY_IMAGE_VALUE);
	}
	
	private Panel getImagePanel(final AccountDTO acct, ValueType imageValueType) {
		HPanel panel = new HPanel();
		Anchor anchor = new Anchor();
		anchor.setHTML(accountFormatter.format(acct, ValueType.TINY_IMAGE_VALUE));
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

	private Heading getHeading(int size, String text) {
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
			StyleHelper.show(conversationRow.getElement(), true);
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
		imageCol.add(getImagePanel(msg.getSender(), ValueType.MEDIUM_IMAGE_VALUE));
		row.add(imageCol);
		
		com.github.gwtbootstrap.client.ui.Column messageCol = new com.github.gwtbootstrap.client.ui.Column(9);
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
				conversation.setStatus(ConversationStatus.READ);
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
				getConversations();
				clearConversationTable();
			}
		});
		container.add(buttonRow);
		root.add(container);
	}
	
	@UiHandler("profileLink")
	void onProfileLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountPlace());
	}
	
	@UiHandler("settingsLink")
	void onSettingsLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountSettingsPlace());
	}
	
	@UiHandler({"messagesLink"})
	void onInboxLinkClicked(ClickEvent event) {
		state.getReceivedMessages();
		getConversations();
		clearConversationTable();
	}
	
	@UiHandler("receivedMessagesLink")
	void displayRecievedMessages(ClickEvent event) {
		state.getReceivedMessages();
		getConversations();
		clearConversationTable();
	}
	
	@UiHandler("sentMessagesLink")
	void displaySentMessages(ClickEvent event) {
		state.getSentMessages();
		getConversations();
		clearConversationTable();
	}

	private void getConversations() {
		//presenter.getConversations(type, pageStart, 0);
		presenter.getConversations(state.getSearchCriteria());
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

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
}
