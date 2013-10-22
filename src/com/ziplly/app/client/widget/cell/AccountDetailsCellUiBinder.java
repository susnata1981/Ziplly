package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.Category;

public class AccountDetailsCellUiBinder extends AbstractCell<AccountDetails> {

	public AccountDetailsCellUiBinder() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void onBrowserEvent(Context ctx, Element parent,
			AccountDetails ad, NativeEvent event, ValueUpdater<AccountDetails> updater) {
//		History.newItem("account/" + ad.account.getAccountId()());
	}
	
	@Override
	public void render(Context context, AccountDetails ad, SafeHtmlBuilder sb) {
		StringBuilder resp = new StringBuilder();
		if (ad != null && ad.account != null) {
			Account a = ad.account;
			resp.append("<div class='account_profile_med'>");
			resp.append("<div class='profile_image_med'><img src='" + a.getImageUrl()+"'/></div>");
			resp.append("<div class='profile_info_med'>");
//			resp.append("<div><span class='heading'>Name:</span> " + a.getDisplayName() + "</div>");
//			resp.append("<div><span class='heading'>Location:</span> " + a.getCity() + "</div>");
			resp.append("<div><span class='heading'>Interests</span><ul>");
			int i = 0;
			for(Category c: ad.categories) {
				i++;
				if (i > 3) {
					break;
				}
				String anchor = "<a href='#category/"+c.getCategoryType().toLowerCase()+"'>"
						+c.getCategoryType().toLowerCase()+"</a>";
				resp.append("<li>"+anchor+"</li>");
			}
			resp.append("</ul></div>");
			resp.append("</div>");
		}
		sb.appendHtmlConstant(resp.toString());
	}
}
