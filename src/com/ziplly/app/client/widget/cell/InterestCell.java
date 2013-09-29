package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.ziplly.app.model.Category;

public class InterestCell extends AbstractCell<Category>{

	public InterestCell() {
		super(BrowserEvents.CLICK);
	}
	
	@Override
	public void render(Context context, Category value, SafeHtmlBuilder sb) {
		StringBuilder resp = new StringBuilder();
		if (value != null) {
			resp.append("<li>");
			resp.append(value.getCategoryType());
			resp.append("&nbsp;");
			resp.append("<a href=''>remove</a>");
			resp.append("</li>");
		}
		sb.appendHtmlConstant(resp.toString());
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Category value,
            NativeEvent event, ValueUpdater<Category> valueUpdater) {
		Window.alert(value.getCategoryType());
	}
}
