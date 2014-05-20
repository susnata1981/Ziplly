package com.ziplly.app.client.widget.chart;

import java.util.Map;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.Options;
import com.googlecode.gwt.charts.client.options.VAxis;

public class LineChartWidget extends AbstractChartWidget<Double> {

	public LineChartWidget(
	  Panel panel, 
	  DataTableAdapter<String, Double> adapter, 
	  String title, 
	  String xAsixTitle, 
	  String yAxisTitle) {
	 
	  super(panel, adapter, title, xAsixTitle, yAxisTitle);
  }

	@Override
  public CoreChartWidget<? extends Options> getChart() {
		chart = new LineChart();
		return chart;
  }

	@Override
  public DataTable buildTable() {
		DataTable dataTable = DataTable.create();
		Map<ChartColumn, Value<Double>> valueMap = getAdapter().getValueMap();
		
		dataTable.addColumn(ColumnType.STRING, xAxisTitle);
		for(ChartColumn col : getAdapter().getColumns()) {
			dataTable.addColumn(ColumnType.NUMBER, col.getName());
		}
		
		int index = 0;
		int totalRows = valueMap.size();
		dataTable.addRows(totalRows);
		for(ChartColumn col : getAdapter().getColumns()) {
			dataTable.setValue(index++, 0, col.getName());
		}
		
		index = 0;
		for(ChartColumn col : valueMap.keySet()) {
			dataTable.setValue(index++, 1, valueMap.get(col).getValue().intValue());
		}

		return dataTable;
  }

	@Override
  public LineChartOptions createOptions(String title) {
		LineChartOptions options = LineChartOptions.create();
		options.setBackgroundColor("#f0f0f0");
		options.setFontName("Tahoma");
		options.setTitle(title);
		options.setHAxis(HAxis.create(xAxisTitle));
		options.setVAxis(VAxis.create(yAxisTitle));
		options.setHeight(400);
		return options;
  }

	@Override
  public void draw() {
		((LineChart)chart).draw(buildTable(), createOptions(getTitle()));
  }
}
