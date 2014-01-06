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
//		injector = GWT.create(ZGinInjector.class);
//		placeController = injector.getPlaceController();
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			PersonalAccountDTO value, SafeHtmlBuilder sb) {
		
		String imgUrl = value.getImageUrl() != null ? 
				value.getImageUrl() : "images/no-photo.jpg";
		if (value != null) {
			sb.appendHtmlConstant(
					" <div class='pcell'>"
					+ "<figure>"
					+ "<img src='"+imgUrl+"'></img>"
					+ "<figcaption>"
					+ "<a href='#'><h2 class='heading'>"+value.getDisplayName()+"</h2></a>"
					+ "</figcaption>");

			sb.appendHtmlConstant(
					"<p class='paragraph'>Lives at:&nbsp;"+value.getNeighborhood().getName()+"</p>"
					+ "</div>");

			if (value.getIntroduction() != null) {
				sb.appendHtmlConstant("About:&nbsp"+value.getIntroduction());
			}
			
			sb.appendHtmlConstant(
				"</div>"
				+ "</figure>" 
				+ "</div>");
		}
	}
}
