package com.ziplly.app.client.widget.chart;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.options.Options;

public abstract class AbstractChartWidget<T> {
	private final Panel panel;
	private final DataTableAdapter<String, T> adapter;
	private String title;
	protected CoreChartWidget<? extends Options> chart;
  protected String yAxisTitle;
  protected String xAxisTitle;
	
	public AbstractChartWidget(Panel panel, DataTableAdapter<String, T> adapter, String title, String xAxisTitle, String yAxisTitle) {
		this.panel = panel;
		this.adapter = adapter;
		this.setTitle(title);
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		initialize();
	}

	protected void initialize() {
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				chart = getChart();
				panel.add(chart);
				buildTable();
				draw();
			}
		});
	}

	public abstract CoreChartWidget<? extends Options> getChart();
	
	public abstract DataTable buildTable();
	
	public abstract Options createOptions(String title);

	public abstract void draw();

	public String getTitle() {
	  return title;
  }

	public void setTitle(String title) {
	  this.title = title;
  }

	public DataTableAdapter<String, T> getAdapter() {
	  return adapter;
  }
}
