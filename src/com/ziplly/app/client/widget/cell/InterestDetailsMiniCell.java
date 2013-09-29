package com.ziplly.app.client.widget.cell;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.ziplly.app.model.CategoryDetails;

public class InterestDetailsMiniCell extends CategoryDetailsCellUiBinder {

	@Override
	public void render(Context context, CategoryDetails value, SafeHtmlBuilder sb) {
		if (value != null) {
			StringBuilder resp = new StringBuilder();
			resp.append("<div class='interestRow'>");
			resp.append("<a>" + value.category.getCategoryType().toLowerCase() + "</a>");
			resp.append("</div>");
			
//			resp.append("<ul>");
//			for(Account a: value.accounts) {
//				resp.append("<li class='category_profiles'>");
//				resp.append("<img src='"+a.getImageUrl()+"' width='60' height='40'>");
//				resp.append("<span class='name'>"+a.getDisplayName()+"</span>");
//				resp.append("</li>");
//			}
//			resp.append("</ul>");
			sb.appendHtmlConstant(resp.toString());
		}
	}
}
