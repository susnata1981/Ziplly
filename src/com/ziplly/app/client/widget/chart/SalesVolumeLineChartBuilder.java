package com.ziplly.app.client.widget.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ziplly.app.client.view.coupon.DateKey;
import com.ziplly.app.model.CouponItemDTO;

public class SalesVolumeLineChartBuilder extends AbstractLineChartBuilder {

  @Override
	public Map<DateKey, Double> aggregateData(final List<CouponItemDTO> couponItems) {
		Map<DateKey, Double> salesPerDate = new HashMap<DateKey, Double>();
		
		for(CouponItemDTO pr : couponItems) {
			DateKey timeCreated = DateKey.get(pr.getTimeCreated());
			if (salesPerDate.get(timeCreated) == null) {
				salesPerDate.put(timeCreated, new Double(0));
			}
			
			Double count = salesPerDate.get(timeCreated);
			salesPerDate.put(timeCreated, count + 1);
		}
		
		return salesPerDate;
	}
}
