package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.AccountNotificationDTO;

public class AccountNotificationFormatter extends AbstractValueFormatter<AccountNotificationDTO> {

	@Override
	public String format(AccountNotificationDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch(type) {
		case RECIPIENT_ACCOUNT_NOTIFICATION_VALUE:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;has a message for you", ValueType.STRING_VALUE));
			return content.toString();
		case ANNOUNCEMENT_NOTIFICATION_VALUE:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;has made a security update", ValueType.STRING_VALUE));
			return content.toString();
		case ACCOUNT_NOTIFICATION_SENDER_IMAGE:
			content.append("<img src='"+value.getSender().getImageUrl()+"' width='25px' height='25px'/>&nbsp;"+value.getSender().getDisplayName());
			return content.toString();
		default:
			throw new IllegalArgumentException("Invalid ValueType to render method");
		}
	}

}
