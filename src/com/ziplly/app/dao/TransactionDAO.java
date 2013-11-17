package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;

public interface TransactionDAO {
	Transaction findById(Long transactionId);
	Transaction findByAccountId(Long accountId);
	TransactionDTO save(Transaction txn) throws DuplicateException;
}
