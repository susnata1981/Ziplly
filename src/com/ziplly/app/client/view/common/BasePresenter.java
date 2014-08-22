package com.ziplly.app.client.view.common;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.resource.StringDefinitions;

public class BasePresenter {
  protected CachingDispatcherAsync dispatcher;
  protected EventBus eventBus;
  protected StringDefinitions stringDefinitions;
  protected ApplicationContext ctx;

  public BasePresenter(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus, 
      StringDefinitions stringDefinitions,
      ApplicationContext ctx) {
    
    assert(dispatcher != null);
    this.dispatcher = dispatcher;
    this.eventBus = eventBus;
    this.stringDefinitions = stringDefinitions;
    this.ctx = ctx;
  }

  public void goTo(PersonalAccountPlace place) {
    eventBus.fireEvent(new PlaceChangeEvent(place));
  }
}
