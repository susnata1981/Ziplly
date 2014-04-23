package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetCouponTransactionAction implements Action<GetCouponTransactionResult>{
	private int start;
	private int pageSize;
	
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
}
