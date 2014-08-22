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
import com.ziplly.app.client.places.AccountSwitcherPlace;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.PlaceParserImpl;
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.community.BusinessViewState;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.shared.FieldVerifier;

public class BusinessAccountCell extends AbstractCell<BusinessAccountDTO> {
	private final BusinessViewState state;
  private final String BASE_URL;

  public BusinessAccountCell(BusinessViewState state) {
    super(BrowserEvents.CLICK);
    this.state = state;
    String environment = System.getProperty(StringConstants.APP_ENVIRONMENT, "devel");
    if (environment.equalsIgnoreCase("DEVEL")) {
      BASE_URL = "http://127.0.0.1:8888/Ziplly.html?gwt.codesvr=127.0.0.1:9997";
    } else {
      BASE_URL = GWT.getHostPageBaseURL();
    }
  }
  
  @Override
	public void onBrowserEvent(Context context,
	    Element parent,
	    BusinessAccountDTO value,
	    NativeEvent event,
	    ValueUpdater<BusinessAccountDTO> valueUpdater) {
		
		if (value == null) {
			return;
		}

		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		long accountId = value.getAccountId();
		NodeList<Element> buttons = parent.getElementsByTagName("button");
		NodeList<Element> anchors = parent.getElementsByTagName("a");
		Element websiteAnchor = getAnchorWithClass(anchors, "website-row-anchor");
		
		Element button = buttons.getItem(0);
		EventTarget target = event.getEventTarget();

		if (button.isOrHasChild(Element.as(target))) {
			BusinessPlace place = new BusinessPlace();
			place.setAccountId(accountId);
			place.setNeighborhoodId(state.getNeighborhoodId());
			place.setPostalCode(state.getPostalCode());
			Window.Location.replace(getRedirectUrl(place));
		} 
		else if (!websiteAnchor.isOrHasChild(Element.as(target))) {
			// Redirect only if it's a click on the profile
		  BusinessAccountPlace place = new BusinessAccountPlace();
		  place.setAccountId(accountId);
			Window.Location.replace(getRedirectUrl(place));
		}
	}

	private String getRedirectUrl(BusinessPlace place) {
	  return BASE_URL + PlaceParserImpl.HASH + AccountSwitcherPlace.TOKEN + PlaceParserImpl.TOP_LEVEL_SEPARATOR + PlaceUtils.getPlaceToken(place);
  }

  private String getRedirectUrl(BusinessAccountPlace place) {
	  return BASE_URL + PlaceParserImpl.HASH + AccountSwitcherPlace.TOKEN + PlaceParserImpl.TOP_LEVEL_SEPARATOR + PlaceUtils.getPlaceToken(place); 
  }

  private Element getAnchorWithClass(NodeList<Element> anchors, String className) {
		int len = anchors.getLength();
		for(int i=0; i<len; i++) {
			Element elem = anchors.getItem(i);
			if (elem.getClassName().equals(className)) {
				return elem;
			}
		}
		
		return null;
  }

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
	    BusinessAccountDTO value,
	    SafeHtmlBuilder sb) {

		if (value == null) {
			return;
		}

		String imgUrl = ImageUtil.getImageUtil(value);
		
		String category =
		    value.getCategory() != null ? value.getCategory().getName() : StringConstants.UNKNOWN;
		String website = !FieldVerifier.isEmpty(value.getWebsite()) ? value.getWebsite() : StringConstants.UNKNOWN;
		StringBuilder locations = new StringBuilder("<ul>");
		for (LocationDTO loc : value.getLocations()) {
			locations.append("<li>");
			locations.append(loc.getNeighborhood().getName() + "," + loc.getNeighborhood().getCity());
			locations.append("</li>");
		}
		locations.append("</ul>");

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
			        + "<span class='pcell-row'>Website: <a class='website-row-anchor' target='_blank' href='"+website+"'>"
			        + website
			        + "</a></span>"
			        + "<span class='pcell-row'> Category: "
			        + category
			        + "</span>"
			        + "<span class='pcell-row'>Locations&nbsp;"
			        + locations.toString()
			        + "</span>"
			        + "<span class='pcell-row'><button class='btn btn-primary btn-mini pcell-btn'>Send Message</button></span>"
			        + "</div>" + "</div>");
		}
	}
}
