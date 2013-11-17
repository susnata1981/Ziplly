package com.ziplly.app.shared;

import com.ziplly.app.model.TransactionDTO;

import net.customware.gwt.dispatch.shared.Result;

public class PayResult implements Result {
	private TransactionDTO transaction;

	public PayResult() {
	}

	public void setTransaction(TransactionDTO txn) {
		this.transaction = txn;
	}
	
	public TransactionDTO getTransaction() {
		return transaction;
	}
}
