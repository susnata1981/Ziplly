package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.Transaction;

public interface TransactionDAO {
	void save(Transaction transaction);
	Transaction update(Transaction transaction);
	Transaction findById(Long transactionId);
	
	List<Transaction> findAllCouponTransactionByAccountId(Long accountId, int start, int pageSize);
	List<Transaction> findByAccountIdAndStatus(Long accountId, TransactionStatus status, int start, int pageSize);
	List<Transaction> findByAccountAndCouponId(Long couponId, Long accountId);
	Transaction findByIdAndStatus(Long transactionId, TransactionStatus status);
	Long findCountByAccountId(Long accountId);
	Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus complete);
	List<Transaction> findAllTransactionByAccountId(Long accountId, int start, int pageSize);
  Transaction findByOrderId(String orderId);
}
