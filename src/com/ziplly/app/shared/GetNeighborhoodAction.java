package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetNeighborhoodAction implements Action<GetNeighborhoodResult>{

	private String postalCode;

	public GetNeighborhoodAction() {
	}
	
	public GetNeighborhoodAction(String postalCode2) {
		this.setPostalCode(postalCode2);
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}
