package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetHashtagAction implements Action<GetHashtagResult>{
	private int size;

	public GetHashtagAction() {
	}
	
	public GetHashtagAction(int n) {
		this.setSize(n);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
