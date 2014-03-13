package com.ziplly.app.client.view.factory;

public class AbstractValueFormatterFactory {

	public static Formatter<?> getValueFamilyFormatter(ValueFamilyType type) {
		switch (type) {
			case ACCOUNT_NOTIFICATION:
				return new AccountNotificationFormatter();
			case PRIVACY_SETTINGS:
				return new PrivacySettingsFormatter();
			case ACCOUNT_NOTIFICATION_SETTINGS:
				return new AccountNotificationSettingsFormatter();
			case ACCOUNT_INFORMATION:
				return new AccountFormatter();
			case BASIC_DATA_VALUE:
				return new BasicDataFormatter();
			default:
				throw new IllegalArgumentException("Invalid ValueFamilyType");
		}
	}

	public static interface Formatter<T> {
		String format(T value, ValueType type);
	}
}
