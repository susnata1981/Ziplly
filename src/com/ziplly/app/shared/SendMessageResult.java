package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.MessageDTO;

public class SendMessageResult implements Result {
	private MessageDTO msg;

	public SendMessageResult() {
	}

	public SendMessageResult(MessageDTO msg) {
		this.setMsg(msg);
	}

	public MessageDTO getMsg() {
		return msg;
	}

	public void setMsg(MessageDTO msg) {
		this.msg = msg;
	}
}
