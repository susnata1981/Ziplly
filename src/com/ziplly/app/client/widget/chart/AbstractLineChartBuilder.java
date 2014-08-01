package com.ziplly.app.client.widget.chart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.gwt.charts.client.ColumnType;
import com.ziplly.app.client.view.coupon.DataBuilder;
import com.ziplly.app.client.view.coupon.DateKey;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponItemDTO;

public abstract class AbstractLineChartBuilder {
  private DataBuilder dataBuilder;

  public AbstractLineChartBuilder() {
    this.dataBuilder = new DataBuilder();
  }
  
  public DataTableAdapter<String, Double> getAdapter(List<CouponDTO> coupons, List<CouponItemDTO> couponItems) {
    Map<DateKey, Double> salesPerDay = aggregateData(couponItems);
    Map<ChartColumn, Value<Double>> dataSet = dataBuilder.populateData(salesPerDay, 30);
    return internalGetAdapter(dataSet);
  }
  
  public DataTableAdapter<String, Double> getAdapter(Map<String, BigDecimal> dataSet) {
    Map<ChartColumn, Value<Double>> cannonicalData = new LinkedHashMap<ChartColumn, Value<Double>>();
    for(String key: dataSet.keySet()) {
      cannonicalData.put(new ChartColumn(key, ColumnType.STRING), new Value<Double>(key, dataSet.get(key).doubleValue()));
    }
    
    return internalGetAdapter(cannonicalData);
  }
  
  private DataTableAdapter<String, Double> internalGetAdapter(final Map<ChartColumn, Value<Double>> dataSet) {
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

  public abstract Map<DateKey, Double> aggregateData(final List<CouponItemDTO> transactions);
}
