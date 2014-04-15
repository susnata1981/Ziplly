package com.ziplly.app.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponCampaignEndedException extends DispatchException implements IsSerializable {

	private static final long serialVersionUID = 1L;
	
	public CouponCampaignEndedException(String errMsg) {
		super(errMsg);
	}
	
	public CouponCampaignEndedException() {
	}

}
