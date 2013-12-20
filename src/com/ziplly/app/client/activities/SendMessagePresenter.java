package com.ziplly.app.client.activities;

import com.ziplly.app.model.ConversationDTO;

public interface SendMessagePresenter extends Presenter {
	void sendMessage(ConversationDTO conversation);
}
