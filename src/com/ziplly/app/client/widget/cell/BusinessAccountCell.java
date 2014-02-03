package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
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
		
//		String redirectUrl = "";
//		String environment = System.getProperty(StringConstants.APP_ENVIRONMENT, "devel");
//		if (environment.equalsIgnoreCase(Environment.DEVEL.name())) {
//			redirectUrl = System.getProperty(StringConstants.REDIRECT_URI, ""); 
//		} else {
//			redirectUrl = GWT.getHostPageBaseURL();
//		}
//		
//		String accountId = value.getAccountId().toString();
//		redirectUrl = redirectUrl + "#personalaccount:" + accountId;
//		Window.Location.replace(redirectUrl);
		String environment = System.getProperty(StringConstants.APP_ENVIRONMENT, "devel");
		String accountId = value.getAccountId().toString();
		String redirectUrl = "";
		
		if (environment.equalsIgnoreCase("DEVEL")) {
			redirectUrl = System.getProperty(StringConstants.REDIRECT_URI, ""); 
		} else {
			redirectUrl = GWT.getHostPageBaseURL();
		}

		NodeList<Element> buttons = parent.getElementsByTagName("button");
		Element button = buttons.getItem(0);
		EventTarget target = event.getEventTarget();
		
		if (button.isOrHasChild(Element.as(target))) {
			redirectUrl = redirectUrl + "#business:" + StringConstants.SEND_MESSAGE_TOKEN + StringConstants.PLACE_SEPARATOR + accountId;
		} else {
			redirectUrl = redirectUrl + "#personalaccount:" + accountId;
		}
		Window.Location.replace(redirectUrl);
	}

	public BusinessAccountCell() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			BusinessAccountDTO value, SafeHtmlBuilder sb) {
		
		if (value == null) {
			return;
		}
		
		String imgUrl = value.getImageUrl() != null ? 
				value.getImageUrl() : "images/no-photo.jpg";
		
		String category = value.getCategory() != null ? value.getCategory().name() : StringConstants.UNKNOWN;
		String website = value.getWebsite() != null ? value.getWebsite() : StringConstants.UNKNOWN;
		
		if (value != null) {
			sb.appendHtmlConstant(
					" <div class='pcell'>"
					+ "<div class='pcell-image'>"
					+ "<img src='"+imgUrl+"'></img>"
					+ "</div>"
					+ "<div class='pcell-info'>"
					+ "<span class='pcell-row-heading'>"+value.getDisplayName()+"</span>"
					+ "<span class='pcell-row'>Website: <a href='#'>"+ website + "</a></span>"
					+ "<span class='pcell-row'> Category: "+ category +"</span>"
					+ "<span class='pcell-row'>Located at:&nbsp;"+value.getNeighborhood().getName()+"</span>"
					+ "<span class='pcell-row'><button class='btn btn-primary btn-mini pcell-btn'>Send Message</button></span>"
					+ "</div>"
					+ "</div>");
		}
	}
}
