package com.ziplly.app.dao;

import com.ziplly.app.model.Coupon;

public interface CouponDAO {
	Coupon findById(Long couponId);
}
