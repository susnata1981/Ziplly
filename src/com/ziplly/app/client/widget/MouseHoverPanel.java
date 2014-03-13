package com.ziplly.app.client.widget;

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;

public class MouseHoverPanel extends HTMLPanel implements HasMouseOverHandlers, HasMouseOutHandlers {
	private MouseOverHandler mouseOverHandler;
	private MouseOutHandler mouseOutHandler;

	public MouseHoverPanel(String html) {
		super(html);
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		this.mouseOverHandler = handler;
		return null;
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEOVER:
				mouseOverHandler.onMouseOver(null);
				break;
			case Event.ONMOUSEOUT:
				mouseOutHandler.onMouseOut(null);
				break;
			default:
		}
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		this.mouseOutHandler = handler;
		return null;
	}
}
