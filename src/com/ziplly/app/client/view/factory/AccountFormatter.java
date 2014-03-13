package com.ziplly.app.client.view.factory;

import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

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
			case PROFILE_IMAGE_URL:
				if (value.getImages().size() > 0) {
					return value.getImages().get(0).getUrl() + "=s300";
				} else {
					return ZResources.IMPL.noPhoto().getSafeUri().asString();
				}
			case PROFILE_BACKROUND_URL:
				if (value.getImages().size() > 0) {
					return value.getImages().get(0).getUrl() + "=s1600";
				} else {
					return ZResources.IMPL.profileBackground().getSafeUri().asString();
				}
			case TINY_IMAGE_VALUE:
				String imgUrl = "";
				if (value.getImageUrl() != null) {
					imgUrl = value.getImageUrl();
				} else {
					imgUrl = ZResources.IMPL.noImage().getSafeUri().asString();
				}
				content.append("<img src='" + imgUrl + "' width='40px' height='40px'/>");
				return content.toString();
			case SMALL_IMAGE_VALUE:
				content.append("<img src='" + value.getImageUrl() + "' width='40px' height='40px'/>&nbsp;");// +
																																																		// value.getDisplayName());
				return content.toString();
			case MEDIUM_IMAGE_ONLY:
				content.append("<img src='" + value.getImageUrl() + "' width='60px' height='60px'/>");
				return content.toString();
			case MEDIUM_IMAGE_VALUE:
				content.append("<img src='" + value.getImageUrl() + "' width='60px' height='60px'/>&nbsp;"
				    + value.getDisplayName());
				return content.toString();
			case BADGE:
				content.append(((PersonalAccountDTO) value).getBadge().getName());
				return content.toString();
			default:
				throw new IllegalArgumentException("Invalid value type");
		}
	}

}
