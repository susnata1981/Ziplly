package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class LogoutAction implements Action<LogoutResult>{
	private Long uid;

	public LogoutAction() {
	}
	
	public LogoutAction(Long uid) {
		this.setUid(uid);
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
}
