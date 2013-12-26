package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.AccountDTO;

public class AccountFormatter extends AbstractValueFormatter<AccountDTO> {

	@Override
	public String format(AccountDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch (type) {
		case EMAIL_VALUE:
			return basicValueFormatter.format(value.getEmail(), ValueType.STRING_VALUE);
		case TIME_CREATED_VALUE:
			return basicValueFormatter.format(value.getTimeCreated(), ValueType.DATE_VALUE_SHORT);
		case NAME_VALUE:
			return basicValueFormatter.format(value.getDisplayName(), ValueType.STRING_VALUE);
		case TINY_IMAGE_VALUE:
			content.append("<img src='" + value.getImageUrl()
					+ "' width='25px' height='25px'/>&nbsp;" + value.getDisplayName());
			return content.toString();
		case SMALL_IMAGE_VALUE:
			content.append("<img src='" + value.getImageUrl()
					+ "' width='40px' height='40px'/>&nbsp;" + value.getDisplayName());
			return content.toString();
		default:
			throw new IllegalArgumentException("Invalid value type");
		}
	}

}
