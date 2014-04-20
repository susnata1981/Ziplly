package com.ziplly.app.client;

import java.io.Serializable;

import com.google.gwt.place.shared.Place;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.GetAccountDetailsResult;

public class ApplicationContext implements Serializable {
  private static final long serialVersionUID = 1L;
  
	public enum Environment {
		DEVEL, 
		PROD;
	}

	private static Environment environment = Environment.DEVEL;
	private static boolean environmentSet = false;
	private static FeatureFlags features;
	private static AccountDetails accountDetails = new AccountDetails();
	
	private Place lastPlace;
	private Place newPlace;

	public ApplicationContext() {
	}

	public AccountDTO getAccount() {
		return accountDetails.account;
	}

	public void setAccount(AccountDTO account) {
		accountDetails.account = account;
	}

	public NeighborhoodDTO getCurrentNeighborhood() {
		return accountDetails.account.getCurrentLocation().getNeighborhood();
	}

	public int getUnreadMessageCount() {
		return accountDetails.unreadMessageCount;
	}

	public int getTotalLikes() {
		return accountDetails.totalLikes;
	}

	public int getTotalComments() {
		return accountDetails.totalComments;
	}

	public int getTotalTweets() {
		return accountDetails.totalTweets;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public boolean isEnvironmentSet() {
		return environmentSet;
	}

	public void setEnvironment(Environment environment) {
		if (environmentSet) {
			return;
		}
		ApplicationContext.environment = environment;
		environmentSet = true;
	}

	public void setAccountDetails(GetAccountDetailsResult result) {
		accountDetails.unreadMessageCount = result.getUnreadMessages();
		accountDetails.totalComments = result.getTotalComments();
		accountDetails.totalLikes = result.getTotalLikes();
		accountDetails.totalTweets = result.getTotalTweets();
	}

	public void setLastPlace(Place place) {
		this.lastPlace = place;
  }
	
	public Place getLastPlace() {
		return lastPlace;
	}

	public void setNewPlace(Place place) {
		if (newPlace != null) {
			lastPlace = newPlace;
		}
		this.newPlace = place;
  }
	
	public Place getNewPlace() {
		return newPlace;
	}
	
	public static FeatureFlags getFeatures() {
	  return features;
  }

	private static class AccountDetails {
		public AccountDTO account;
		public int unreadMessageCount;
		public int totalTweets;
		public int totalComments;
		public int totalLikes;
	}
}
