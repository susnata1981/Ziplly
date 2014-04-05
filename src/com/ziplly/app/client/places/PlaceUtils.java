package com.ziplly.app.client.places;

import com.google.gwt.core.client.GWT;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Gender;
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
		    + StringConstants.PLACE_SEPARATOR + tweet.getTweetId();
	}
}
