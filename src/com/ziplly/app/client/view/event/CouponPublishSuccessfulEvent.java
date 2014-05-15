package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CouponPublishSuccessfulEvent extends GwtEvent<CouponPublishSuccessfulEvent.Handler>{
	public static final Type<CouponPublishSuccessfulEvent.Handler> TYPE = new Type<CouponPublishSuccessfulEvent.Handler>();
	
	public static interface Handler extends EventHandler {
		void onEvent(CouponPublishSuccessfulEvent event);
	}

	@Override
  protected void dispatch(Handler handler) {
		handler.onEvent(this);
  }
	
	@Override
  public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
  }
}
