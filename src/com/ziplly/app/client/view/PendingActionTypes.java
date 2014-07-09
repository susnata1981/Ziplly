package com.ziplly.app.client.view;

import com.google.gwt.place.shared.Place;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;


public enum PendingActionTypes {
  INCOMPLETE_ACCOUNT_SETTINGS() {
    @Override
    public String text() {
      return "Account incomplete";
    }

    @Override
    public Place getPlace() {
      return new BusinessAccountSettingsPlace(BusinessAccountSettingsPlace.SettingsTab.BUSINESS_DETAILS);
    }

    @Override
    public String learnMoreText() {
      return "";
    }
  },
  SUBSCRIPTION_REQUIRED() {

    @Override
    public String text() {
      return "Pending subscription";
    }

    @Override
    public Place getPlace() {
      return new BusinessAccountSettingsPlace(BusinessAccountSettingsPlace.SettingsTab.SUBSCRIPTION_PLANS);
    }

    @Override
    public String learnMoreText() {
      return "To publish coupons, you need to subscribe to a plan. Payment plans are free for the first 60 days,"
          + "so you won't be charged anything till then";
    }

  };
  
  public abstract String text();
  public abstract String learnMoreText();
  public abstract Place getPlace();
}
