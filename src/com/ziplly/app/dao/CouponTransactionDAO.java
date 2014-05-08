package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.TransactionStatus;

public interface CouponTransactionDAO {
	void save(CouponTransaction couponTransaction);
	CouponTransaction update(CouponTransaction couponTransaction);
	List<CouponTransaction> findAllCouponTransactionByAccountId(Long accountId, int start, int pageSize);
	List<CouponTransaction> findByAccountIdAndStatus(Long accountId, TransactionStatus status, int start, int pageSize);
	List<CouponTransaction> findByAccountAndCouponId(Long couponId, Long accountId);
	Coupon findByCouponId(Long couponId);
	CouponTransaction findById(Long couponTransactionId);
	CouponTransaction findByIdAndStatus(Long transactionId,
			TransactionStatus status);
	Long findCountByAccountId(Long accountId);
	Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus complete);
	List<CouponTransaction> findCouponTransactionByCouponId(Long couponId, int start, int pageSize);
	Long getTotalCountByByCouponId(Long couponId);
}
