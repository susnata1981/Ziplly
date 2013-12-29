package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.AccountNotificationSettingsDTO;

public class AccountNotificationSettingsFormatter extends
		AbstractValueFormatter<AccountNotificationSettingsDTO> {

	@Override
	public String format(AccountNotificationSettingsDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch (type) {
		case ACCOUNT_NOTIFICATION_TYPE:
			content.append("<span>" + value.getType().name().toLowerCase() + "</span>");
			return basicValueFormatter.format(content.toString(), ValueType.STRING_VALUE);
		default:
			throw new IllegalArgumentException("invalid type to format method");
		}
	}
}
