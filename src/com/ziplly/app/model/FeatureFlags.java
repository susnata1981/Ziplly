package com.ziplly.app.model;

public enum FeatureFlags {
  RegistrationRestcricted(false),
  EnableCouponFeature(true),
  OneCouponPerIndividual(true),
  EnablePaymentPlan(false),
  EnableCityCheck(true);

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

  public static boolean hasPermissionToPublishCoupon(NeighborhoodDTO currentNeighborhood) {
    return currentNeighborhood.getCity().equalsIgnoreCase("SEATTLE");
  }
}