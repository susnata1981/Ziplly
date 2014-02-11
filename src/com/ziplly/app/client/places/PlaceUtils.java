package com.ziplly.app.client.places;

import com.ziplly.app.client.view.StringConstants;

public class PlaceUtils {
	
	public static String getPlaceTokenForNeighborhood(Long neighborhoodId) {
		return StringConstants.NEIGHBORHOOD_TOKEN + StringConstants.PLACE_SEPARATOR + neighborhoodId;
	}
	
	public static String getPlaceTokenForMessaging(Long accountId) {
		return StringConstants.SEND_MESSAGE_TOKEN + StringConstants.PLACE_SEPARATOR + accountId;
	}
}
