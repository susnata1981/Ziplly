package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponCampaignNotStartedException extends DispatchException {

	private static final long serialVersionUID = 1L;
	
	public CouponCampaignNotStartedException(String errMsg) {
		super(errMsg);
	}
	
	public CouponCampaignNotStartedException() {
	}

}
