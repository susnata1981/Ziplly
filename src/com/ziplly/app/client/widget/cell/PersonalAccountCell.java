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
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.ResidentViewState;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountCell extends AbstractCell<PersonalAccountDTO> {
	// ZGinInjector injector = GWT.create(ZGinInjector.class);
	// private PlaceController placeController;

	private ResidentViewState state;

	public PersonalAccountCell(ResidentViewState state) {
		super(BrowserEvents.CLICK);
		this.state = state;
	}

	@Override
	public void onBrowserEvent(Context context,
	    Element parent,
	    PersonalAccountDTO value,
	    NativeEvent event,
	    ValueUpdater<PersonalAccountDTO> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if (value == null) {
			return;
		}

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
//			redirectUrl =
//			    redirectUrl + "#residents:" + StringConstants.SEND_MESSAGE_TOKEN
//			        + StringConstants.PLACE_SEPARATOR + accountId;
			redirectUrl = redirectUrl + "#residents:" + PlaceUtils.getPlaceTokenForMessaging(value.getAccountId(), state.getNeighborhoodId(), state.getGender());
		} else {
			redirectUrl = redirectUrl + "#personalaccount:" + accountId;
		}

		Window.Location.replace(redirectUrl);
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
	    PersonalAccountDTO value,
	    SafeHtmlBuilder sb) {

		if (value == null) {
			return;
		}

		String imgUrl = ImageUtil.getImageUtil(value);

		String introduction =
		    value.getIntroduction() != null ? value.getIntroduction() : "Not Available";

		if (value != null) {
			sb
			    .appendHtmlConstant(" <div class='pcell'>"
			        + "<div class='pcell-image'>"
			        + "<img src='"
			        + imgUrl
			        + "'></img>"
			        + "</div>"
			        + "<div class='pcell-info'>"
			        + "<span class='pcell-row-heading'>"
			        + value.getDisplayName()
			        + "</span>"
			        + "<span class='pcell-row'><span class='pcell-row-info-heading'>Gender:</span>&nbsp;"
			        + value.getGender().name().toLowerCase()
			        + "</span>"
			        + "<span class='pcell-row'><span class='pcell-row-info-heading'>Introduction:</span>&nbsp;"
			        + introduction
			        + "</span>"
			        + "<span class='pcell-row'><span class='pcell-row-info-heading'>Locations</span>&nbsp;"
			        + value.getLocations().get(0).getNeighborhood().getName()
			        + "</span>"
			        + "<span class='pcell-row'><button class='btn btn-primary btn-mini pcell-btn'>Send Message</button></span>"
			        + "</div>" + "</div>");
		}
	}
}
