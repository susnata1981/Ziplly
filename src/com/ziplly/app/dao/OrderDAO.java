package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.OrderStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.server.model.jpa.Order;

public interface OrderDAO {
	Order findById(Long purchaseCouponId);
	
	void save(Order order);
	Order update(Order pr);
	
	List<CouponItem> findTransactionByCouponId(Long couponId, int start, int pageSize);
	
	Long getTotalCountByByCouponId(Long couponId);
	
	List<CouponItem> findByAccountIdAndStatus(
			Long accountId,
      TransactionStatus status,
      int start,
      int pageSize);
	
	Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus complete);
	
	List<CouponItem> findByTransactionId(Long couponTransactionId);
	
	List<CouponItem> findByTransactionIdAndStatus(Long transactionId, TransactionStatus pending);

	List<CouponItem> findByAccountAndCouponId(Long couponId, Long accountId, OrderStatus completed);

  CouponItem findCouponItemByOrderAndCouponId(long orderId, long couponId);
}
