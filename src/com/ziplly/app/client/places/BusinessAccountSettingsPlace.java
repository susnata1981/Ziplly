package com.ziplly.app.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BusinessAccountSettingsPlace extends Place {
	private SettingsTab tab;
	private static SettingsTab defaultTab = SettingsTab.BUSINESS_DETAILS;
	
  public BusinessAccountSettingsPlace(SettingsTab tab) {
	  this.setTab(tab);
	}

  public BusinessAccountSettingsPlace() {
    this(defaultTab);
  }
  
	public SettingsTab getTab() {
    return tab;
  }

  public void setTab(SettingsTab tab) {
    this.tab = tab;
  }

  @Prefix("businesssettings")
	public static class Tokenizer implements PlaceTokenizer<BusinessAccountSettingsPlace> {
		@Override
		public BusinessAccountSettingsPlace getPlace(String token) {
		  try {
		    if (token != null) {
		       SettingsTab tab = SettingsTab.valueOf(token);
		       return new BusinessAccountSettingsPlace(tab);
	      }
        return new BusinessAccountSettingsPlace(defaultTab);
		  } catch(Exception ex) {
		    return new BusinessAccountSettingsPlace(defaultTab);
		  }
		}

		@Override
		public String getToken(BusinessAccountSettingsPlace place) {
			return place.getTab().name();
		}
	}
  
  public enum SettingsTab {
    BUSINESS_DETAILS(0),
    SUBSCRIPTION_PLANS(1),
    TRANSACTION_DETAILS(2),
    NOTIFICATION(3),
    PASSWORD_RESET(4);
    
    private int position;

    SettingsTab(int pos) {
      this.position = pos;
    }

    public int getPosition() {
      return position;
    }
  }
}
