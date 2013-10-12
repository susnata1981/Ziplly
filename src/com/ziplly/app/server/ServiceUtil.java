package com.ziplly.app.server;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.ziplly.app.model.Category;
import com.ziplly.app.shared.facebook.FacebookUserInterest;

public class ServiceUtil {
	public static List<Category> transform(FacebookUserInterest fui) {
		List<Category> result = Lists.newArrayList();
		for (String interest : fui.interests) {
			Category c = new Category();
			c.setCategory(SafeHtmlUtils.fromString(interest).asString().trim()
					.toUpperCase());
			result.add(c);
		}
		return result;
	}
}
