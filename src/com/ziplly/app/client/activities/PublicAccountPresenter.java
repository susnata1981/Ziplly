package com.ziplly.app.client.activities;

import com.ziplly.app.model.MessageDTO;

public interface PublicAccountPresenter extends Presenter {
	void sendMessage(MessageDTO msg);
}
