package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.model.PriceRange;


public class PriceRangeWidget extends Widget {
	
	int max = 3;
	
	public PriceRangeWidget(PriceRange range) {
		final SpanElement rangeElement = Document.get().createSpanElement();
		setElement(rangeElement);
		setStyleName("price-range");
		setRange(range);
	}

	public PriceRangeWidget() {
		this(PriceRange.MEDIUM);
	}
	
	public void setRange(PriceRange range) {
		int r = range.ordinal() + 1;
		System.out.println("ORDINAL:"+r);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<r; i++) {
			sb.append("$");
		}
		getElement().setInnerHTML(sb.toString());
	}
	
	public void clear() {
		getElement().setInnerHTML("");
	}
}
