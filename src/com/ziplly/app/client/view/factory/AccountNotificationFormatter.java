package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.AccountNotificationDTO;

public class AccountNotificationFormatter extends AbstractValueFormatter<AccountNotificationDTO> {

	@Override
	public String format(AccountNotificationDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch(type) {
		case PERSONAL_MESSAGE:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;has a message for you", ValueType.STRING_VALUE));
			return content.toString();
		case SECURITY_ALERT:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;posted a security alert", ValueType.STRING_VALUE));
			return content.toString();
		case ANNOUNCEMENT:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;posted an announcement", ValueType.STRING_VALUE));
			return content.toString();
		case OFFERS:
			content.append(format(value, ValueType.ACCOUNT_NOTIFICATION_SENDER_IMAGE));
			content.append(basicValueFormatter.format("&nbsp;posted an offer", ValueType.STRING_VALUE));
			return content.toString();
		case ACCOUNT_NOTIFICATION_SENDER_IMAGE:
			content.append("<img src='"+value.getSender().getImageUrl()+"' width='25px' height='25px'/>&nbsp;"+value.getSender().getDisplayName());
			return content.toString();
		case ACCOUNT_NOTIFICATION_TYPE:
			content.append("<span>"+value.getType().name().toLowerCase()+"</span>");
			return content.toString();
		default:
			throw new IllegalArgumentException("Invalid ValueType to render method");
		}
	}

}
