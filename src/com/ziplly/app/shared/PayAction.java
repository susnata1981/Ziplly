package com.ziplly.app.shared;

import com.ziplly.app.model.TransactionDTO;

import net.customware.gwt.dispatch.shared.Action;

public class PayAction implements Action<PayResult> {
	private TransactionDTO transaction;

	public PayAction() {
	}
	
	public PayAction(TransactionDTO txn) {
		this.setTransaction(txn);
	}

	public TransactionDTO getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionDTO transaction) {
		this.transaction = transaction;
	}
}
