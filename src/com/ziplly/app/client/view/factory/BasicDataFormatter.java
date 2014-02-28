package com.ziplly.app.client.view.factory;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.TweetType;

public class BasicDataFormatter implements Formatter<Object> {

	@Override
	public String format(Object value, ValueType type) {
		switch (type) {
		case INTEGER_VALUE:
		case STRING_VALUE:
			return value.toString();
		case DATE_VALUE_MEDIUM:
			String date = DateTimeFormat.getFormat(
					PredefinedFormat.DATE_TIME_MEDIUM).format((Date)value);
			return date;
		case DATE_VALUE:
		case DATE_VALUE_SHORT:
			date = DateTimeFormat.getFormat(
					PredefinedFormat.DATE_SHORT).format((Date)value);
			return date;
		case UNREAD_MESSAGE_COUNT:
			return "("+ value.toString()+")";
		case GENDER:
			return ((Gender)value).getName();
		case TWEET_TYPE:
			return ((TweetType)value).getTweetName();
		case NOTIFICATION_TYPE:
			return ((NotificationType)value).getNotificationName();
		case PROFILE_IMAGE_URL:
			String imgUrl = (String) value;
			if (imgUrl == null) {
				imgUrl = ZResources.IMPL.noImage().getSafeUri().asString();
			}
			return imgUrl;
		case ADDRESS:
			return value.toString();
		case NEIGHBORHOOD:
			NeighborhoodDTO n = (NeighborhoodDTO) value;
			if (n.getParentNeighborhood() != null) {
				return n.getName() + ", " + n.getCity();
			} else {
				return n.getName();
			}
		case NEIGHBORHOOD_IMAGE:
			NeighborhoodDTO neighborhood = (NeighborhoodDTO) value;
			if (neighborhood.getImageUrl() != null) {
				return neighborhood.getImageUrl() + "=s1600";
			} else if (neighborhood.getParentNeighborhood() != null) {
				imgUrl = (neighborhood.getParentNeighborhood().getImageUrl() != null) ? neighborhood.getParentNeighborhood().getImageUrl()
						: "";
				return imgUrl + "=s1600";
			}
		default:
			throw new IllegalArgumentException("Invalid value type to render");
		}
	}

}
