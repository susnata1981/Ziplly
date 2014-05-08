package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Coupon;

public interface CouponDAO {
	Coupon findById(Long couponId);

	List<Coupon> findCouponsByAccountId(Long accountId, int start, int pageSize);

	Long getTotalCouponCountByAccountId(Long accountId);
}
