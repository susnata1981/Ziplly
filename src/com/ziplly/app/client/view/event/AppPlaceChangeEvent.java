package com.ziplly.app.client.view.event;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;

public class AppPlaceChangeEvent extends PlaceChangeEvent {

  public AppPlaceChangeEvent(Place newPlace) {
    super(newPlace);
  }

}
