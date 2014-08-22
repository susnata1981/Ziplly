package com.ziplly.app.client.conversation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
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
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.AccountSwitcherPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.resource.TableResources;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.ConversationViewState;
import com.ziplly.app.client.view.IConversationView;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.shared.GetConversationsAction;

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
	private static ConversationViewUiBinder uiBinder = GWT.create(ConversationViewUiBinder.class);

	interface ConversationViewUiBinder extends UiBinder<Widget, ConversationView> {
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
		conversationTable.setBordered(true);
		conversationTable.setHover(true);
		conversationTable.setStriped(true);
		conversationTable.setEmptyTableWidget(new Label("No conversations"));
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
				return;
			}
			
			conversationTable.setRowData(state.getStart(), conversations);
			highlightUnreadConversation();
		}
	}

	private void highlightUnreadConversation() {
		int start = state.getStart();
		int end = start + PAGE_SIZE;
		if (conversations == null) {
			return;
		}
		int totalConversations = conversations.size();

		for (int i = start; i < end; i++) {
			if (i >= totalConversations) {
				return;
			}
			ConversationDTO c = conversations.get(i);
			if (/* !c.isSender() && */c.getStatus() == ConversationStatus.UNREAD) {
				TableRowElement row = conversationTable.getRowElement(i - start);
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
			elem.setInnerHTML("(" + value + ")");
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
		Column<ConversationDTO, String> senderColumn =
		    new Column<ConversationDTO, String>(new ClickableTextCell() {
			    public void render(Context context, String value, SafeHtmlBuilder sb) {
				    ConversationDTO c;
				    c = conversations.get(context.getIndex() - state.getStart());
				    sb.appendHtmlConstant(c.getSender().getDisplayName());//getImageLink(c.getSender()));
			    }
		    }) {
			    @Override
			    public String getValue(ConversationDTO c) {
				    return accountFormatter.format(c.getSender(), ValueType.PROFILE_IMAGE_URL);
			    }
		    };
		conversationTable.addColumn(senderColumn, buildHeader(SENDER_KEY));
		conversationTable.setColumnWidth(senderColumn, 20, Unit.PCT);

		Column<ConversationDTO, String> receiverColumn =
		    new Column<ConversationDTO, String>(new ClickableTextCell() {
			    public void render(Context context, String value, SafeHtmlBuilder sb) {
				    ConversationDTO c;
				    c = conversations.get(context.getIndex() - state.getStart());
				    sb.appendHtmlConstant(c.getReceiver().getDisplayName());//getImageLink(c.getReceiver()));
			    }
		    }) {
			    @Override
			    public String getValue(ConversationDTO c) {
				    return accountFormatter.format(c.getSender(), ValueType.PROFILE_IMAGE_URL);
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
				return basicDataFormatter.format(
				    c.getMessages().get(0).getTimeCreated(),
				    ValueType.DATE_DIFF);
			}
		};
		conversationTable.addColumn(timeSentCol, buildHeader(TIME_RECEIVED_KEY));
		conversationTable.setColumnWidth(timeSentCol, 26, Unit.PCT);

		conversationTable.addCellPreviewHandler(new Handler<ConversationDTO>() {

			@Override
			public void onCellPreview(CellPreviewEvent<ConversationDTO> event) {
				boolean isClick = "click".equals(event.getNativeEvent().getType());
				if (isClick) {
					displayConversation(event.getValue());
				}
			}
		});
	}

	@Override
	public void setPresenter(ConversationViewPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void updateConversation(ConversationDTO conversation) {
	  cdv.displayConversation(conversation);
	}

	final ConversationDetailsView cdv = new ConversationDetailsView();
	public void displayConversation(final ConversationDTO conversation) {
	  conversationPanel.clear();
	  cdv.displayConversation(conversation);
	  conversationPanel.add(cdv);
	  presenter.onView(conversation);
	  cdv.getReplyButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (!cdv.validateReplyContent()) {
          return;
        }
        
        Date now = new Date();
        MessageDTO m = new MessageDTO();
        m.setMessage(cdv.getReplyContent());
        m.setTimeCreated(now);
        if (conversation.isSender()) {
          m.setReceiver(conversation.getReceiver());
          m.setSender(conversation.getSender());
        } else {
          m.setReceiver(conversation.getSender());
          m.setSender(conversation.getReceiver());
        }
        conversation.add(m);
        conversation.setTimeUpdated(now);
        presenter.sendMessage(conversation);
        cdv.clear();
      }
	  });
	  
	  cdv.getBackButton().addClickHandler(new ClickHandler(){

      @Override
      public void onClick(ClickEvent event) {
        getConversations();
        clearConversationTable();
      }
	    
	  });
	}

	@UiHandler("profileLink")
	void onProfileLinkClick(ClickEvent event) {
		presenter.goTo(new AccountSwitcherPlace());
	}

	@UiHandler("settingsLink")
	void onSettingsLinkClick(ClickEvent event) {
		presenter.goTo(new PersonalAccountSettingsPlace());
	}

	@UiHandler({ "messagesLink" })
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
		// presenter.getConversations(type, pageStart, 0);
		presenter.getConversations(state.getSearchCriteria());
	}

	private void setInboxLink(INBOXLINK link) {
		for (INBOXLINK l : INBOXLINK.values()) {
			if (l == link) {
				inboxLinks.get(l).getElement().getParentElement().getStyle().setBackgroundColor("#ddd");
			} else {
				inboxLinks.get(l).getElement().getParentElement().getStyle().clearBackgroundColor();
			}
		}
	}

	public static enum INBOXLINK {
		INBOX, RECEIVED_MESSAGES, SENT_MESSAGES
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
