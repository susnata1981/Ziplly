package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessAccountCell extends AbstractCell<BusinessAccountDTO> {

	@Override
	public void onBrowserEvent(Context context, Element parent, BusinessAccountDTO value,
			NativeEvent event, ValueUpdater<BusinessAccountDTO> valueUpdater) {
		if (value == null) {
			return;
		}
		
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		
		String redirectUrl = "";
		String environment = System.getProperty(StringConstants.APP_ENVIRONMENT, "devel");
		if (environment.equalsIgnoreCase(Environment.DEVEL.name())) {
			redirectUrl = System.getProperty(StringConstants.REDIRECT_URI, ""); 
		} else {
			redirectUrl = GWT.getHostPageBaseURL();
		}
		
		String accountId = value.getAccountId().toString();
		redirectUrl = redirectUrl + "#personalaccount:" + accountId;
		Window.Location.replace(redirectUrl);
	}

	public BusinessAccountCell() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			BusinessAccountDTO value, SafeHtmlBuilder sb) {
		
		String imgUrl = value.getImageUrl() != null ? 
				value.getImageUrl() : "images/no-photo.jpg";
		
		String category = value.getCategory() != null ? value.getCategory().name() : "";
		String website = value.getWebsite() != null ? value.getWebsite() : "website";
		
		if (value != null) {
			sb.appendHtmlConstant(
					" <div class='pcell'>"
					+ "<figure>"
					+ "<img src='"+imgUrl+"'></img>"
					+ "<figcaption>"
					+ "<div>"
					+ "<span class='communityHeading'>"+value.getDisplayName()+"</span>"
					+ "<span class='communityParagraph'>Website: <a href='#'>"+ website + "</a></span>"
					+ "<br/><span class='communityParagraph'> Category: "+ category +"</span>"
					+ "</div>");

			sb.appendHtmlConstant(
					"<p><span class='communityParagraph'>Located at:&nbsp;</span>"+value.getNeighborhood().getName()+"</p>"
					+ "</div>"
					+ "</figcaption>");

			
			
			sb.appendHtmlConstant(
				"</div>"
				+ "</figure>" 
				+ "</div>");
		}
	}
}
