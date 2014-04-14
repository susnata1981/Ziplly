package com.ziplly.app.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponCampaignEnded extends DispatchException implements IsSerializable {

	private static final long serialVersionUID = 1L;
	
	public CouponCampaignEnded(String couponDescription) {
		super(String.format("Coupon: %s discount ended", couponDescription));
	}
	
	public CouponCampaignEnded() {
	}

}
