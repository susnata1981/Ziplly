package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.TransactionStatus;

public interface CouponTransactionDAO {
	void save(CouponTransaction couponTransaction);
	CouponTransaction update(CouponTransaction couponTransaction);
	List<CouponTransaction> findAllCouponTransactionByAccountId(Long accountId, int start, int pageSize);
	List<CouponTransaction> findCouponTransactionByAccountIdAndStatus(Long accountId, TransactionStatus status, int start, int pageSize);
	List<CouponTransaction> findCouponTransactionByAccountAndCouponId(Long couponId, Long accountId);
	Coupon findByCouponId(Long couponId);
	CouponTransaction findById(Long couponTransactionId);
	CouponTransaction findCouponTransactionByIdAndStatus(Long transactionId,
			TransactionStatus status);
	Long findCouponTransactionCountByAccountId(Long accountId);
}
