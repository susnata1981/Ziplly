package com.ziplly.app.client.exceptions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class CouponCampaignNotStarted extends DispatchException {

	private static final long serialVersionUID = 1L;
	
	public CouponCampaignNotStarted(String couponDescription) {
		super(String.format("Coupon:%s discount campaign not strated", couponDescription));
	}
	
	public CouponCampaignNotStarted() {
	}

}
