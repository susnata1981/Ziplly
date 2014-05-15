package com.ziplly.app.client.widget.chart;

import java.util.List;
import java.util.Map;

public interface DataTableAdapter<T,V> {
	public List<ChartColumn> getColumns();
	public Map<ChartColumn, Value<V>> getValueMap();
}
