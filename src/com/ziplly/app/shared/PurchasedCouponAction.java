package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CouponDTO;

public class PurchasedCouponAction implements Action<PurchaseCouponResult> {
	public enum ResultStatus {
		SUCCESS,
		FAILED;
	}
	
	private AccountDTO buyer;
	private CouponDTO coupon;
	private String couponTransactionId;
	private ResultStatus resultStatus;
	
	public PurchasedCouponAction() {
  }
	
	public CouponDTO getCoupon() {
	  return coupon;
  }

	public void setCoupon(CouponDTO coupon) {
	  this.coupon = coupon;
  }

	public AccountDTO getBuyer() {
	  return buyer;
  }

	public void setBuyer(AccountDTO buyer) {
	  this.buyer = buyer;
  }

	public String getCouponTransactionId() {
		return couponTransactionId;
	}

	public void setCouponTransactionId(String couponTransactionId) {
		this.couponTransactionId = couponTransactionId;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
}
