package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ZAnchor extends Widget implements HasClickHandlers {
	private Element anchorElement; 
	
	public ZAnchor() {
		anchorElement = Document.get().createElement("a");
		setupWidget();
	}

	public ZAnchor(Element elem) {
		assert(elem.getTagName().equalsIgnoreCase("a"));
		this.anchorElement = elem;
		setupWidget();
	}
	
	public static ZAnchor wrap(Element elem) {
		assert Document.get().getBody().isOrHasChild(elem);
		assert elem.getTagName().equalsIgnoreCase("a");
		
		ZAnchor anchor = new ZAnchor(elem);
		anchor.onAttach();
		RootPanel.detachOnWindowClose(anchor);
		return anchor;
	}
	
	public void setWidth(int w) {
		anchorElement.getStyle().setWidth(w, Unit.PX);
	}
	
	public void setText(String text) {
		anchorElement.setInnerHTML(text);
	}
	
	public void setIcon(ZipllyIcon icon) {
		anchorElement.addClassName(icon.name());
	}
	
	public void setIcon(String icon) {
		anchorElement.addClassName("fontawesome");
		ZipllyIcon zicon = ZipllyIcon.valueOf(icon);
		anchorElement.addClassName(zicon.getName());
	}
	
	private void setupWidget() {
		setElement(anchorElement);
		anchorElement.addClassName("fontawesome");
		sinkEvents(Event.ONCLICK);
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return this.addDomHandler(handler, ClickEvent.getType());
	}

}
