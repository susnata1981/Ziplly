package com.ziplly.app.client.places;

import com.ziplly.app.client.view.StringConstants;

public class PlaceUtils {
	
	public static String getResidentPlaceTokenForNeighborhood(Long neighborhoodId) {
		return StringConstants.NEIGHBORHOOD_TOKEN + StringConstants.PLACE_SEPARATOR + neighborhoodId;
	}
}
