package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.widget.dataprovider.ConversationDataProvider;
import com.ziplly.app.model.Conversation;

public class ConversationWidget extends AbstractAccountView {
	private static final int PAGE_SIZE = 10;

	private static ConversationWidgetUiBinder uiBinder = GWT
			.create(ConversationWidgetUiBinder.class);

	interface ConversationWidgetUiBinder extends UiBinder<Widget, ConversationWidget> {
	}

	@UiField(provided=true)
	SimplePager pager;
	
	@UiField(provided=true)
	CellTable<Conversation> table;
	
	@UiField
	TextBox subject;
	
	@UiField
	TextArea message;
	
	@UiField
	SubmitButton sendBtn;
	
	ConversationDataProvider dataProvider;
	
	public ConversationWidget(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		dataProvider = new ConversationDataProvider(this, service);
//		System.out.println("Account ID="+this.getAd().account.getDisplayName());
		dataProvider.addDataDisplay(table);
	}

	@Override
	protected void setupUiElements() {
		table = new CellTable<Conversation>();
		buildTable();
		pager = new SimplePager();
		pager.setDisplay(table);
		table.setPageSize(PAGE_SIZE);
	}

	void buildTable() {
		TextColumn<Conversation> sender = new TextColumn<Conversation>() {
			@Override
			public String getValue(Conversation c) {
				return c.getSender().getDisplayName();
			}
		};
		table.addColumn(sender, "Sender");
		
		TextColumn<Conversation> subject = new TextColumn<Conversation>() {
			@Override
			public String getValue(Conversation c) {
				System.out.println("C="+c.getMessages().size());
				if (c.getMessages().size()>0) {
					return c.getMessages().get(0).getSubject();
				}
				return "";
			}
		};
		table.addColumn(subject, "Subject");
		
		final SingleSelectionModel<Conversation> selectionModel = new SingleSelectionModel<Conversation>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Conversation c = selectionModel.getSelectedObject();
				Window.alert(c.getMessages().get(0).getSubject()+" selected!");
			}
		});
	}

	@Override
	protected void internalOnUserLogin() {
	}
}
