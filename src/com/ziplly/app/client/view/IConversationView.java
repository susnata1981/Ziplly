package com.ziplly.app.client.view;

import java.util.List;

import com.ziplly.app.client.view.ConversationView.ConversationViewPresenter;
import com.ziplly.app.model.ConversationDTO;

public interface IConversationView extends View<ConversationViewPresenter>{
	void displayConversations(List<ConversationDTO> conversations);
	void displayConversation(ConversationDTO conversation);
	void updateConversation(ConversationDTO c);
}
