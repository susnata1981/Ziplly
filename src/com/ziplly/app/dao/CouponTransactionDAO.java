package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;

public interface CouponTransactionDAO {
	void save(CouponTransaction couponTransaction);
	CouponTransaction update(CouponTransaction couponTransaction);
	List<CouponTransaction> findCouponTransactionByAccountId(Long accountId);
	Coupon findByCouponId(Long couponId);
}
