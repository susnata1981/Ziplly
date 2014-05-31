package com.ziplly.app.client.widget.chart;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.options.Options;
import com.ziplly.app.client.view.StringConstants;

public abstract class AbstractChartWidget<T> extends Composite {
	private final DataTableAdapter<String, T> adapter;
	private String title;
	protected CoreChartWidget<? extends Options> chart;
  protected String yAxisTitle;
  protected String xAxisTitle;
  private Alert message;
	private FlowPanel panel;
	
	public AbstractChartWidget(DataTableAdapter<String, T> adapter, String title, String xAxisTitle, String yAxisTitle) {
		this.adapter = adapter;
		this.setTitle(title);
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.panel = new FlowPanel();
		message = new Alert();
		message.setClose(false);
		initialize();
		panel.add(message);
		message.setVisible(false);
		initWidget(panel);
	}

	protected void initialize() {
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				chart = getChart();
				buildTable();
				draw();
				panel.add(chart);
			}
		});
	}

	public abstract CoreChartWidget<? extends Options> getChart();
	
	public abstract DataTable buildTable();
	
	public abstract Options createOptions(String title);

	public abstract void internalDraw();

	private void draw() {
	  message.setVisible(false);
	  DataTable buildTable = buildTable();
    if (buildTable.getNumberOfRows() == 0) {
      displayMessage(StringConstants.NO_DATA, AlertType.WARNING);
      return;
    }
    
	  internalDraw();
	}
	
	public void displayMessage(String msg, AlertType type) {
	  message.setText(msg);
	  message.setType(type);
	  message.setVisible(true);
	}
	
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
