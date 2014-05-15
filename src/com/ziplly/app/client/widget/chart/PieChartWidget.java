package com.ziplly.app.client.widget.chart;

import java.util.Map;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;

public class PieChartWidget {
	private PieChart chart;
	private final Panel panel;
	private final DataTableAdapter<String, Integer> adapter;
	private String title;

	public PieChartWidget(Panel panel, DataTableAdapter<String, Integer> adapter, String title) {
		this.panel = panel;
		this.adapter = adapter;
		this.title = title;
		initialize();
	}

	private void initialize() {
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				chart = new PieChart();
				panel.add(chart);
				buildTable();
				draw();
			}
		});
	}

	private DataTable buildTable() {
		DataTable dataTable = DataTable.create();
		Map<ChartColumn, Value<Integer>> valueMap = adapter.getValueMap();
		
		for(ChartColumn col : adapter.getColumns()) {
			dataTable.addColumn(col.getColumnType(), col.getName());
		}
		
		int totalRows = valueMap.size();
		dataTable.addRows(totalRows);
		int index = 0;
		for(Value<Integer> value : valueMap.values()) {
			dataTable.setValue(index, 0, value.getLabel());
			dataTable.setValue(index, 1, value.getValue());
			index++;
		}
		
		return dataTable;
  }

	public PieChartOptions createOptions(String title) {
		PieChartOptions options = PieChartOptions.create();
		options.setBackgroundColor("#f0f0f0");

		// options.setColors(colors);
		options.setFontName("Tahoma");
		options.setIs3D(true);
		options.setPieResidueSliceColor("#000000");
		options.setPieResidueSliceLabel("Others");
		options.setSliceVisibilityThreshold(0.1);
		options.setTitle(title);
		return options;
	}
	
	private void draw() {
		DataTable table = buildTable();
		PieChartOptions options = createOptions(title);
		chart.draw(table, options);
	}
	
//	private void draw() {
//		// Prepare the data
//		DataTable dataTable = DataTable.create();
//		dataTable.addColumn(ColumnType.STRING, "Task");
//		dataTable.addColumn(ColumnType.NUMBER, "Hours per Day");
//		dataTable.addRows(5);
//		dataTable.setValue(0, 0, "Work");
//		dataTable.setValue(0, 1, 11);
//		dataTable.setValue(1, 0, "Sleep");
//		dataTable.setValue(1, 1, 7);
//		dataTable.setValue(2, 0, "Watch TV");
//		dataTable.setValue(2, 1, 3);
//		dataTable.setValue(3, 0, "Eat");
//		dataTable.setValue(3, 1, 2);
//		dataTable.setValue(4, 0, "Commute");
//		dataTable.setValue(4, 1, 1);
//
//		// Set options
//		PieChartOptions options = PieChartOptions.create();
//		options.setBackgroundColor("#f0f0f0");
//
//		// options.setColors(colors);
//		options.setFontName("Tahoma");
//		options.setIs3D(true);
//		options.setPieResidueSliceColor("#000000");
//		options.setPieResidueSliceLabel("Others");
//		options.setSliceVisibilityThreshold(0.1);
//		options.setTitle("So, how was your day?");
//
//		// Draw the chart
//		chart.draw(dataTable, options);
//	}
}
