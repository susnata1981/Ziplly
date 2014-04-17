package com.ziplly.app.model;

public enum FeatureFlags {
	EnableCouponFeature(true),
	EnablePaymentPlan(false);
	
	private boolean enable;

	private FeatureFlags(boolean enable) {
		this.setEnable(enable);
  }

	public boolean isEnable() {
	  return enable;
  }

	public void setEnable(boolean enable) {
	  this.enable = enable;
  }
}