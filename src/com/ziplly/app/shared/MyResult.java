package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

public class MyResult implements Result{
	private String message;

	public MyResult() {
	}
	
	public MyResult(String msg) {
		this.message = msg;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
