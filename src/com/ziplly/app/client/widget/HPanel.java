package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Replacement for HorizontalPanel, which is inefficient and bloated as it uses 
 * tables. This uses css display - block and inline-block :-) 
 * @author shaan
 */
public class HPanel extends ComplexPanel {
	
	public HPanel() {
		setElement(Document.get().createDivElement());
		getElement().addClassName("horizontal-panel");
	}
	
	@Override
 	public void add(Widget w) {
		if (w != null) {
			w.setStyleName("horizontal-widget");
			super.add(w, getElement());
		}
 	}
	
	public Widget getWidget(int index) {
		if (index < 0 || index >= getWidgetCount()) {
			throw new IllegalArgumentException("Invalid index to getWidget(...)");
		}
		
		return super.getWidget(index);
	}

	public void insert(Widget widget, int beforeIndex) {
		super.insert(widget, getElement(), beforeIndex, true);
	}
	
	public void setStyleName(String style) {
		super.addStyleName(style);
	}
}
