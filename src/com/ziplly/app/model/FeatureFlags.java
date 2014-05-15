package com.ziplly.app.model;

public enum FeatureFlags {
	EnableCouponFeature(true),
	OneCouponPerIndividual(true),
	EnablePaymentPlan(true);
	
	private boolean enable;

	private FeatureFlags(boolean enable) {
		this.setEnable(enable);
  }

	public boolean isEnabled() {
	  return enable;
  }

	public void setEnable(boolean enable) {
	  this.enable = enable;
  }
}