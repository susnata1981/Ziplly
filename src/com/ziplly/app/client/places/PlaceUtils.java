package com.ziplly.app.client.places;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;

public class PlaceUtils {

	public static String getPlaceTokenForNeighborhood(Long neighborhoodId) {
		return StringConstants.NEIGHBORHOOD_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR + neighborhoodId;
	}

	/**
	 * For Personal Accounts
	 * @param accountId
	 * @param neighborhoodId
	 * @param gender
	 * @return
	 */
	public static String getPlaceTokenForMessaging(Long accountId, Long neighborhoodId, Gender gender) {
		return StringConstants.SEND_MESSAGE_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR  + accountId + StringConstants.PLACE_SEPARATOR  +
				StringConstants.NEIGHBORHOOD_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR + neighborhoodId + StringConstants.PLACE_SEPARATOR  +
				StringConstants.GENDER_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR + gender.name();
	}

	/**
	 * For Business
	 * @param accountId
	 * @param neighborhoodId
	 * @return
	 */
	public static String getPlaceTokenForMessaging(Long accountId, Long neighborhoodId) {
		return StringConstants.SEND_MESSAGE_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR + accountId + StringConstants.PLACE_SEPARATOR  +
				StringConstants.NEIGHBORHOOD_TOKEN + StringConstants.PLACE_VALUE_SEPARATOR + neighborhoodId; 
	}
	
	public static String toString(BusinessAccountSettingsPlace place) {
	  return GWT.getHostPageBaseURL() + "#" + "businesssettings" + 
        StringConstants.PLACE_SEPARATOR  +
        place.getTab().name();
	}
	
	public static String getHomePlaceTokenForMessaging(Long tweetId) {
		return StringConstants.HOME_TWEET_TOKEN + StringConstants.PLACE_SEPARATOR + tweetId.toString();
	}

	public static String getHomePlaceTokenForTweetType(String tweetType) {
		// return StringConstants.HOME_TOKEN + StringConstants.PLACE_SEPARATOR +
		// tweetType;
		return tweetType;
	}

	public static String getPlaceTokenForTweetDetails(TweetDTO tweet) {
		return GWT.getHostPageBaseURL() + "#" + StringConstants.TWEET_DETAILS_TOKEN
		    + StringConstants.PLACE_SEPARATOR 
		    + tweet.getTweetId();
	}
	
	public static AccountPlace getPlace(AccountDTO account) {
		if (account == null) {
			Window.alert("Error encountered.");
			return null;
		}
		
		return (account instanceof PersonalAccountDTO) ? new PersonalAccountPlace() : new BusinessAccountPlace();
	}
}
