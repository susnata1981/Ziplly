package com.ziplly.app.client.view.event;

import com.google.gwt.event.shared.GwtEvent;
import com.ziplly.app.client.view.handler.CategorySelectedEventHandler;

public class CategorySelectedEvent extends GwtEvent<CategorySelectedEventHandler> {

	public static final Type<CategorySelectedEventHandler> TYPE =
	    new Type<CategorySelectedEventHandler>();
	private String category;

	public CategorySelectedEvent(String category) {
		this.setCategory(category);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CategorySelectedEventHandler>
	    getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorySelectedEventHandler handler) {
		handler.onEvent(this);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
