package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetCouponsAction implements Action<GetCouponsResult>{
	private int start;
	private int pageSize;
	private Long accountId;

	public GetCouponsAction() {
  }
	
	public GetCouponsAction(Long accountId) {
		this.accountId = accountId;
  }
	
	public Long getAccountId() {
	  return accountId;
  }

	public void setAccountId(Long accountId) {
	  this.accountId = accountId;
  }

	public int getPageSize() {
	  return pageSize;
  }

	public void setPageSize(int pageSize) {
	  this.pageSize = pageSize;
  }

	public int getStart() {
	  return start;
  }

	public void setStart(int start) {
	  this.start = start;
  }
}
