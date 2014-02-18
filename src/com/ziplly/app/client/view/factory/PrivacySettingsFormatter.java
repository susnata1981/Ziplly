package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.PrivacySettingsDTO;

public class PrivacySettingsFormatter extends AbstractValueFormatter<PrivacySettingsDTO>{

	@Override
	public String format(PrivacySettingsDTO value, ValueType type) {
		StringBuilder content = new StringBuilder();
		switch (type) {
		case PRIVACY_FIELD_NAME:
			content.append(value.getSection().getName());
			return basicValueFormatter.format(content.toString(), ValueType.STRING_VALUE);
		default:
			throw new IllegalArgumentException("invalid type to format method");
		}
	}
}
