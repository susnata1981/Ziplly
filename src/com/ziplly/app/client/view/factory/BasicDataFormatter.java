package com.ziplly.app.client.view.factory;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.model.Gender;

public class BasicDataFormatter implements Formatter<Object> {

	@Override
	public String format(Object value, ValueType type) {
		switch (type) {
		case INTEGER_VALUE:
		case STRING_VALUE:
			return value.toString();
		case DATE_VALUE_MEDIUM:
			String date = DateTimeFormat.getFormat(
					PredefinedFormat.DATE_TIME_MEDIUM).format((Date)value);
			return date;
		case DATE_VALUE:
		case DATE_VALUE_SHORT:
			date = DateTimeFormat.getFormat(
					PredefinedFormat.DATE_SHORT).format((Date)value);
			return date;
		case UNREAD_MESSAGE_COUNT:
			return "("+ value.toString()+")";
		case GENDER:
			return ((Gender)value).getName();
		default:
			throw new IllegalArgumentException("Invalid value tyoe to render");
		}
	}

}
