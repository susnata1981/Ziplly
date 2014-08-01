package com.ziplly.app.shared;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponItemDTO;

public class GetCouponsResult implements Result {
  private Map<CouponDTO, List<CouponItemDTO>> couponTransactionMap =
      new HashMap<CouponDTO, List<CouponItemDTO>>();

  private Map<String, BigDecimal> salesAmountData = new LinkedHashMap<String, BigDecimal>();
  
	private long totalCouponCount;

	public List<CouponDTO> getCoupons() {
	  return new ArrayList<CouponDTO>(couponTransactionMap.keySet());
  }

	public void setTotalCouponCount(long totalCouponCount) {
		this.totalCouponCount = totalCouponCount;
  }
	
	public long getTotalCouponCount() {
		return totalCouponCount;
	}
	
	public void addTransactions(CouponDTO coupon, CouponItemDTO transaction) {
	  if (!couponTransactionMap.containsKey(coupon)) {
	    couponTransactionMap.put(coupon, new ArrayList<CouponItemDTO>());
	  }
	  
	  couponTransactionMap.get(coupon).add(transaction);
	}
	
	public void addTransactions(CouponDTO coupon, List<CouponItemDTO> transaction) {
    if (!couponTransactionMap.containsKey(coupon)) {
      couponTransactionMap.put(coupon, new ArrayList<CouponItemDTO>());
    }
    
    couponTransactionMap.get(coupon).addAll(transaction);
  }
	
	public List<CouponItemDTO> getCouponItems() {
	  List<CouponItemDTO> couponItems = new ArrayList<CouponItemDTO>();
	  for(List<CouponItemDTO> couponItem : couponTransactionMap.values()) {
	    couponItems.addAll(couponItem);
	  }
	  
	  return couponItems;
	}

  public Map<CouponDTO, List<CouponItemDTO>> getCouponTransactionMap() {
    return Collections.unmodifiableMap(couponTransactionMap);
  }

  public Map<String, BigDecimal> getSalesAmountData() {
    return Collections.unmodifiableMap(salesAmountData);
  }

  public void setSalesAmountData(Map<String, BigDecimal> salesAmountData) {
    this.salesAmountData = salesAmountData;
  }
}
