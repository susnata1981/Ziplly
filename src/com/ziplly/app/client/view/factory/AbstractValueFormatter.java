package com.ziplly.app.client.view.factory;

import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;

public abstract class AbstractValueFormatter<T> implements Formatter<T> {
	protected Formatter<Object> basicValueFormatter;

	@SuppressWarnings("unchecked")
	public AbstractValueFormatter() {
		basicValueFormatter =
		    (Formatter<Object>) AbstractValueFormatterFactory
		        .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
	}
}
