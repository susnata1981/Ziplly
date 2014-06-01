package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.PurchasedCoupon;

public interface PurchasedCouponDAO {
	PurchasedCoupon findById(Long purchaseCouponId);
	
	void save(PurchasedCoupon pr);
	PurchasedCoupon update(PurchasedCoupon pr);
	
	List<PurchasedCoupon> findTransactionByCouponId(Long couponId, int start, int pageSize);
	
	Long getTotalCountByByCouponId(Long couponId);
	
	List<PurchasedCoupon> findByAccountIdAndStatus(
			Long accountId,
      TransactionStatus status,
      int start,
      int pageSize);
	
	Long getTotalByAccountIdAndStatus(Long accountId, TransactionStatus complete);
	
	List<PurchasedCoupon> findByTransactionId(Long couponTransactionId);
	
	List<PurchasedCoupon> findByTransactionIdAndStatus(Long transactionId, TransactionStatus pending);

	List<PurchasedCoupon> findByAccountAndCouponId(Long couponId, Long accountId);
}
