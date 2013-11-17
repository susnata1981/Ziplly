package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetLatLngAction implements Action<GetLatLngResult> {
	private int zip;

	public GetLatLngAction() {
	}
	
	public GetLatLngAction(int zip) {
		this.setZip(zip);
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}
}
	