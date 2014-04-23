package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.CouponTransactionDTO;

public class GetCouponTransactionResult implements Result {
	private List<CouponTransactionDTO> transactions = new ArrayList<CouponTransactionDTO>();
	private Long totalTransactions;
	
	public List<CouponTransactionDTO> getTransactions() {
	  return transactions;
  }

	public void setTransactions(List<CouponTransactionDTO> transactions) {
	  this.transactions = transactions;
  }

	public Long getTotalTransactions() {
	  return totalTransactions;
  }

	public void setTotalTransactions(Long totalTransactions) {
	  this.totalTransactions = totalTransactions;
  }
}
