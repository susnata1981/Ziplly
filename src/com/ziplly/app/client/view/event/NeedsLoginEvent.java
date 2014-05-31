package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class NeedsLoginEvent extends GwtEvent<NeedsLoginEvent.Handler>{
  public static Type<NeedsLoginEvent.Handler> TYPE = new Type<NeedsLoginEvent.Handler>();

  @Override
  public Type<NeedsLoginEvent.Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(NeedsLoginEvent.Handler handler) {
    handler.onEvent(this);
  }

  public static interface Handler extends EventHandler {
    public void onEvent(NeedsLoginEvent event);
  }
}
