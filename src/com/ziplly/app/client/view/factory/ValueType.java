package com.ziplly.app.client.view.factory;

public enum ValueType {
	// General
	STRING_VALUE,
	TEXT_VALUE,
	DATE_VALUE_MONTH_DAY,
	DATE_VALUE,
	DATE_VALUE_SHORT,
	INTEGER_VALUE,
	DATE_VALUE_MEDIUM,
	UNREAD_MESSAGE_COUNT,
	NOTIFICATION_TYPE,

	// Tweet
	TWEET_TYPE,

	// Account Notification
	PERSONAL_MESSAGE,
	SECURITY_ALERT,
	ANNOUNCEMENT,
	OFFERS,
	ACCOUNT_NOTIFICATION_SENDER_IMAGE,

	// Account
	EMAIL_VALUE,
	NAME_VALUE,
	TIME_CREATED_VALUE,
	PROFILE_IMAGE_URL,
	PROFILE_BACKROUND_URL,
	TINY_IMAGE_VALUE,
	SMALL_IMAGE_VALUE,
	MEDIUM_IMAGE_ONLY,
	MEDIUM_IMAGE_VALUE,
	LARGE_IMAGE_VALUE,
	GENDER,
	BADGE,

	// Personal Account
	PERSON_NAME_VALUE,
	PERSON_IMAGE_VALUE,

	// Business Account
	BUSINESS_NAME_VALUE,
	BUSINESS_IMAGE_VALUE,
	BUSINESS_ADDRESS_VALUE,

	// Privacy Settings
	PRIVACY_FIELD_NAME,

	// Account Notification Settings
	ACCOUNT_NOTIFICATION_TYPE,

	// Neighborhood
	NEIGHBORHOOD,
	FOUND_NEIGHBORHOOD_MESSAGE,
	NEIGHBORHOOD_IMAGE,
	ADDRESS,
	POSTAL_CODE,
	
	// Coupon
	COUPON, 
	PRICE, 
	PERCENT, 
}
