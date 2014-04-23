package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;

public interface CouponTransactionDAO {
	void save(CouponTransaction couponTransaction);
	CouponTransaction update(CouponTransaction couponTransaction);
	List<CouponTransaction> findCouponTransactionByAccountId(Long accountId, int start, int pageSize);
	List<CouponTransaction> findCouponTransactionByAccountAndCouponId(Long couponId, Long accountId);
	Coupon findByCouponId(Long couponId);
	Long findCouponTransactionCountByAccountId(Long accountId);
	CouponTransaction findById(Long couponTransactionId);
}
