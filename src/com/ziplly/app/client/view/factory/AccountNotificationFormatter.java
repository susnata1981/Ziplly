package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.AccountNotificationDTO;

public class AccountNotificationFormatter extends AbstractValueFormatter<AccountNotificationDTO> {

	@Override
	public String format(AccountNotificationDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch (type) {
			case PERSONAL_MESSAGE:
				content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
				content.append("&nbsp;" + value.getSender().getDisplayName());
				content.append(basicValueFormatter.format(
				    "&nbsp;has a message for you",
				    ValueType.STRING_VALUE));
				return content.toString();
			case SECURITY_ALERT:
				content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
				content.append("&nbsp;" + value.getSender().getDisplayName()
				    + basicValueFormatter.format("&nbsp;posted a security alert", ValueType.STRING_VALUE));
				return content.toString();
			case ANNOUNCEMENT:
				content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
				content.append("&nbsp;" + value.getSender().getDisplayName());
				content.append(basicValueFormatter.format(
				    "&nbsp;posted an announcement",
				    ValueType.STRING_VALUE));
				return content.toString();
			case OFFERS:
				content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
				content.append("&nbsp;" + value.getSender().getDisplayName());
				content.append(basicValueFormatter.format("&nbsp;posted an offer", ValueType.STRING_VALUE));
				return content.toString();
			case ACCOUNT_NOTIFICATION_SENDER_IMAGE:
				content.append("<img src='"
				    + basicValueFormatter.format(
				        value.getSender().getImageUrl(),
				        ValueType.PROFILE_IMAGE_URL) + "' width='25px' height='25px'/>");
				return content.toString();
			case ACCOUNT_NOTIFICATION_TYPE:
				content.append(value.getType().getNotificationName());
				return content.toString();
			default:
				throw new IllegalArgumentException("Invalid ValueType to render method");
		}
	}

}
