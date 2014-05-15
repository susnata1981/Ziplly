package com.ziplly.app.client.widget.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.gwt.charts.client.ColumnType;

public class ColumnTypeMap {
	private static Map<Class<?>, ColumnType> columnTypeMap = new HashMap<Class<?>, ColumnType>();
	
	static {
		columnTypeMap.put(String.class, ColumnType.STRING);
		columnTypeMap.put(Integer.class, ColumnType.NUMBER);
		columnTypeMap.put(Date.class, ColumnType.DATE );
	}
	
	public static ColumnType getClass(Class<?> clazz) {
		return columnTypeMap.get(clazz);
	}
}
