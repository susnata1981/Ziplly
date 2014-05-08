package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetCouponTransactionAction implements Action<GetCouponTransactionResult>{
	private int start;
	private int pageSize;
	private Long couponId;
	private SearchType searchType = SearchType.BY_ACCOUNT_ID;
	
	public int getStart() {
	  return start;
  }
	public void setStart(int start) {
	  this.start = start;
  }
	public int getPageSize() {
	  return pageSize;
  }
	public void setPageSize(int pageSize) {
	  this.pageSize = pageSize;
  }
	
	public SearchType getSearchType() {
	  return searchType;
  }
	public void setSearchType(SearchType searchType) {
	  this.searchType = searchType;
  }

	public Long getCouponId() {
	  return couponId;
  }
	public void setCouponId(Long couponId) {
	  this.couponId = couponId;
  }

	public enum SearchType {
		BY_ACCOUNT_ID,
		BY_COUPON_ID
	}
}
