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
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountCell extends AbstractCell<PersonalAccountDTO> {
	boolean devel = true;
	
	@Override
	public void onBrowserEvent(Context context, Element parent, PersonalAccountDTO value,
			NativeEvent event, ValueUpdater<PersonalAccountDTO> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if (value == null) {
			return;
		}

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
	
	public PersonalAccountCell() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			PersonalAccountDTO value, SafeHtmlBuilder sb) {
		
		if (value == null) {
			return;
		}
		
		String imgUrl = value.getImageUrl() != null ? 
				value.getImageUrl() : "images/no-photo.jpg";
		
		String introduction = value.getIntroduction() != null ? value.getIntroduction() : "Not Available";
		
		if (value != null) {
			sb.appendHtmlConstant(
					" <div class='pcell'>"
					+ "<div class='pcell-image'>"
					+ "<img src='"+imgUrl+"'></img>"
					+ "</div>"
					+ "<div class='pcell-info'>"
					+ "<span class='pcell-row-heading'>"+value.getDisplayName()+"</span>"
					+ "<span class='pcell-row'><span class='pcell-row-info-heading'>Gender:</span>&nbsp;"+ value.getGender().name().toLowerCase() + "</span>"
					+ "<span class='pcell-row'><span class='pcell-row-info-heading'>Introduction:</span>&nbsp;"+ introduction +"</span>"
					+ "<span class='pcell-row'><span class='pcell-row-info-heading'>Location:</span>&nbsp;"+value.getNeighborhood().getName()+"</span>"
					+ "</div>"
					+ "</div>");
		}
	}
}
