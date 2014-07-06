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
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PlaceParserImpl;
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.ResidentViewState;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountCell extends AbstractCell<PersonalAccountDTO> {
  private ResidentViewState state;
  private String BASE_URL;

  public PersonalAccountCell(ResidentViewState state) {
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
      PersonalAccountDTO value,
      NativeEvent event,
      ValueUpdater<PersonalAccountDTO> valueUpdater) {
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
    if (value == null) {
      return;
    }

    long accountId = value.getAccountId();

    NodeList<Element> buttons = parent.getElementsByTagName("button");
    Element button = buttons.getItem(0);
    EventTarget target = event.getEventTarget();

    if (button.isOrHasChild(Element.as(target))) {
      ResidentPlace place = new ResidentPlace();
      place.setAccountId(accountId);
      place.setNeighborhoodId(state.getNeighborhoodId());
      place.setGender(state.getGender());
      Window.Location.replace(getRedirectUrl(place));
    } else {
      // Redirect only if it's a click on the profile
      PersonalAccountPlace place = new PersonalAccountPlace();
      place.setAccountId(accountId);
      Window.Location.replace(getRedirectUrl(place));
    }
  }

  private String getRedirectUrl(ResidentPlace place) {
    return BASE_URL + PlaceParserImpl.HASH + ResidentPlace.TOKEN
        + PlaceParserImpl.TOP_LEVEL_SEPARATOR + PlaceUtils.getPlaceToken(place);
  }

  private String getRedirectUrl(PersonalAccountPlace place) {
    return BASE_URL + PlaceParserImpl.HASH + BusinessAccountPlace.TOKEN
        + PlaceParserImpl.TOP_LEVEL_SEPARATOR + PlaceUtils.getPlaceToken(place);
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
        value.getIntroduction() != null ? value.getIntroduction() : StringConstants.NOT_AVAILABLE;

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
              + "<span class='pcell-row'><span class='pcell-row-info-heading'>Gender</span>&nbsp;"
              + value.getGender().name().toLowerCase()
              + "</span>"
              + "<span class='pcell-row'><span class='pcell-row-info-heading'>Introduction</span>&nbsp;"
              + introduction
              + "</span>"
              + "<span class='pcell-row'><span class='pcell-row-info-heading'>Location</span>&nbsp;"
              + value.getLocations().get(0).getNeighborhood().getName()
              + "</span>"
              + "<span class='pcell-row'><button class='btn btn-primary btn-mini pcell-btn'>Send Message</button></span>"
              + "</div>" + "</div>");
    }
  }
}
