package com.ziplly.app.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.widget.ConversationWidget;
import com.ziplly.app.model.ConversationDTO;

public class ConversationView extends Composite implements IConversationView {

	public interface ConversationViewPresenter extends Presenter {
		void getConversations();
		void sendMessage(ConversationDTO conversation);
	}
	
	private static ConversationViewUiBinder uiBinder = GWT
			.create(ConversationViewUiBinder.class);

	interface ConversationViewUiBinder extends
			UiBinder<Widget, ConversationView> {
	}

	@UiField
	HTMLPanel conversationPanel;
	
	@UiField
	ConversationWidget conversationWidget;

	private ConversationViewPresenter presenter;
	
	public ConversationView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void clear() {
		conversationWidget.clear();
	}

	@Override
	public void displayConversations(List<ConversationDTO> conversations) {
		conversationWidget.clear();
		conversationWidget.displayConversations(conversations);
	}

	@Override
	public void setPresenter(ConversationViewPresenter presenter) {
		this.presenter = presenter;
		conversationWidget.setPresenter(presenter);
	}

	@Override
	public void updateConversation(ConversationDTO c) {
		conversationWidget.updateConversation(c);
	}
}
