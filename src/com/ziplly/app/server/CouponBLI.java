package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;

import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.model.Coupon;

public interface CouponBLI {
	String getQrcode(Long buyerAccountId, Coupon coupon) throws InternalError;
}
