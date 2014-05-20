package com.ziplly.app.client.widget.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.googlecode.gwt.charts.client.ColumnType;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.shared.GetCouponTransactionResult;

public abstract class AbstractLineChartBuilder {
  private Map<ChartColumn, Value<Double>> dataSet = new HashMap<ChartColumn, Value<Double>>();
  private BasicDataFormatter formatter = new BasicDataFormatter();

  public DataTableAdapter<String, Double> getAdapter(final GetCouponTransactionResult result) {
    Map<Date, Double> salesPerDay = aggregateData(result);
    buildDataSet(salesPerDay);
    return internalGetAdapter(salesPerDay);
  }
  
  private DataTableAdapter<String, Double> internalGetAdapter(final Map<Date, Double> salesPerDate) {
    DataTableAdapter<String, Double> adapter = new DataTableAdapter<String, Double>() {

      @Override
      public List<ChartColumn> getColumns() {
        return new ArrayList<ChartColumn>(dataSet.keySet());
      }

      @Override
      public Map<ChartColumn, Value<Double>> getValueMap() {
        return dataSet;
      }
    };

    return adapter;
  }
  
  private void buildDataSet(final Map<Date, Double> salesPerDay) {
    for(Date date : salesPerDay.keySet()) {
      String key = formatter.format(date, ValueType.DATE_VALUE_MONTH_DAY);
      ChartColumn col1 = new ChartColumn(key, ColumnType.NUMBER);
      dataSet.put(col1, 
          new Value<Double>(key, salesPerDay.get(date)));
    }
  }

  protected Map<Date, Double> createNoDataSet(Date start, Date end) {
    Map<Date, Double> salesPerDate = new HashMap<Date, Double>();
    int totalDays = CalendarUtil.getDaysBetween(start, end);
    for(int i = 0; i < totalDays; i++) {
      Date currDate = (Date) start.clone();
      CalendarUtil.addDaysToDate(currDate, i);
      salesPerDate.put(currDate, new Double(0));
    }
    return salesPerDate;
  }
  
  public abstract Map<Date, Double> aggregateData(final GetCouponTransactionResult result);
}
