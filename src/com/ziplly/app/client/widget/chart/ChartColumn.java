package com.ziplly.app.client.widget.chart;

import com.googlecode.gwt.charts.client.ColumnType;

public class ChartColumn {
	private String name;
	private ColumnType columnType;
	
	public ChartColumn(String name, ColumnType colType) {
		this.name = name;
		this.columnType = colType;
  }
	
	public String getName() {
	  return name;
  }

	public void setName(String name) {
	  this.name = name;
  }

	public ColumnType getColumnType() {
	  return columnType;
  }

	public void setColumnType(ColumnType columnType) {
	  this.columnType = columnType;
  }
}
