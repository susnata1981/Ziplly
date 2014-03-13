package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.TransactionDTO;

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
