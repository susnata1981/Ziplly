package com.ziplly.app.client.places;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;

public class PlaceUtils {
	private static final String HASH = "#";

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
	  return GWT.getHostPageBaseURL() + HASH + "businesssettings" + 
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
		return GWT.getHostPageBaseURL() + HASH + StringConstants.TWEET_DETAILS_TOKEN
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

  public static String getPlaceToken(PrintCouponPlace place) {
    return "" + place.getOrderId() + StringConstants.PLACE_SEPARATOR + place.getCouponId();
  }

  public static String getPlaceToken(PersonalAccountPlace place) {
    return getAccountPlaceToken(place);
  }
  
  public static String getPlaceToken(BusinessAccountPlace place) {
    return getAccountPlaceToken(place);
  }
  
  private static String getAccountPlaceToken(AccountPlace place) {
    return AttributeKey.ACCOUNT_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getAccountId() 
        + PlaceParserImpl.PLACE_SEPARATOR 
        + AttributeKey.TRANSACTION_VIEW_TOKEN.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.isShowTransactions();
  }

  public static String getPlaceToken(ConversationPlace place) {
    return AttributeKey.CONVERSATION_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getConversationId(); 
  }

  public static String getPlaceToken(HomePlace place) {
    return AttributeKey.TWEET_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getTweetId() 
        + PlaceParserImpl.PLACE_SEPARATOR 
        + AttributeKey.TWEET_CATEGORY.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getTweetType();
  }

  public static String getPlaceToken(TweetDetailsPlace place) {
    return AttributeKey.TWEET_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getTweetId(); 
  }

  public static String getPlaceToken(BusinessPlace place) {
    return AttributeKey.SEND_MESSAGE_TOKEN.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getAccountId() 
        + PlaceParserImpl.PLACE_SEPARATOR 
        + AttributeKey.NEIGHBORHOOD_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getNeighborhoodId()
        + PlaceParserImpl.PLACE_SEPARATOR
        + AttributeKey.POSTAL_CODE.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getPostalCode();
  }
  
  public static String getPlaceToken(ResidentPlace place) {
    return AttributeKey.SEND_MESSAGE_TOKEN.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getAccountId() 
        + PlaceParserImpl.PLACE_SEPARATOR 
        + AttributeKey.NEIGHBORHOOD_ID.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getNeighborhoodId()
        + PlaceParserImpl.PLACE_SEPARATOR
        + AttributeKey.GENDER_KEY.getName() + PlaceParserImpl.VALUE_SEPARATOR + place.getGender();
  }
}
