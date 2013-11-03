package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;

public class AccountDetailsMiniCell extends AbstractCell<AccountDetails> {

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			AccountDetails value, SafeHtmlBuilder sb) {
		// TODO Auto-generated method stub
		
	}

//	public AccountDetailsMiniCell() {
//		super(BrowserEvents.CLICK);
//	}
//
//	@Override
//	public void onBrowserEvent(Context context, Element parent,
//			AccountDetails value, NativeEvent event,
//			ValueUpdater<AccountDetails> valueUpdater) {
//		History.newItem("account/" + value.account.getId());
//	}
//
//	@Override
//	public void render(Context context, AccountDetails ad, SafeHtmlBuilder sb) {
//		StringBuilder resp = new StringBuilder();
//		if (ad != null && ad.account != null) {
//			Account a = ad.account;
//			resp.append("<div class='account_profile_mini'>");
//			resp.append("<img class='profile_image_mini' src='" + a.getImageUrl()+"'/>");
//			resp.append("<div class='profile_info_mini'>");
//			resp.append("<div class='text'>Name: " + a.getDisplayName() + "</span>");
//			resp.append("<div class='text'>Location: " + a.getCity() + "</span>");
//			resp.append("</div>");
//			resp.append("</div>");
//		}
//		sb.appendHtmlConstant(resp.toString());
//	}
//
//	void handleClick(ClickEvent event, Element parent, Account account,
//			Context ctx, ValueUpdater<Account> updater) {
//		Window.alert("Cell clicked!");
//	}
}
