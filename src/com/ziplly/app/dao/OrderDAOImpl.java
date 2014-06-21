package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.CouponItemStatus;
import com.ziplly.app.model.OrderStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.server.model.jpa.Order;

public class OrderDAOImpl extends BaseDAO implements OrderDAO {
	private Logger logger = Logger.getLogger(TransactionDAOImpl.class.getName());

	@Inject
	public OrderDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	@Transactional
	public void save(Order order) {
		entityManagerProvider.get().persist(order);
	}

	@Override
	@Transactional
	public Order update(Order order) {
		return entityManagerProvider.get().merge(order);
	}
	
	@Override
	public List<CouponItem> findTransactionByCouponId(Long couponId, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		Query query =
		    em
		        .createQuery("from CouponItem item where item.coupon.couponId = :couponId and status in (:status)")
		        .setParameter("couponId", couponId)
		        .setParameter("status", ImmutableList.of(CouponItemStatus.UNUSED.name(), CouponItemStatus.USED.name()))
		        .setFirstResult(start)
		        .setMaxResults(pageSize);

		return query.getResultList();
	}
	
	@Override
	public Long getTotalCountByByCouponId(Long couponId) {
		EntityManager em = entityManagerProvider.get();
		Query query =
		    em.createQuery("select count(item) from CouponItem item where item.coupon.couponId = :couponId and status in (:status)")
		        .setParameter("couponId", couponId)
						.setParameter("status", ImmutableList.of(CouponItemStatus.UNUSED.name(), CouponItemStatus.USED.name()));
		return (Long) query.getSingleResult();
	}
	
	@Override
  public List<CouponItem> findByAccountIdAndStatus(Long accountId, TransactionStatus status, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select od.item from Order o join o.orderDetails od "
		    + "where o.transaction.buyer.accountId = :accountId and o.transaction.status = :status")
				    .setParameter("accountId", accountId)
				    .setParameter("status", status.name())
				    .setFirstResult(start)
				    .setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<CouponItem> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(od.item) from Order o join o.orderDetails od "
        + "where o.transaction.buyer.accountId = :accountId and o.transaction.status = :status")
				     .setParameter("accountId", accountId)
				     .setParameter("status", status.name());
		return (Long) query.getSingleResult();
  }

	@Override
  public List<CouponItem> findByTransactionId(Long transactionId) {
		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.transactionId = :transactionId")
//				.setParameter("transactionId", transactionId);
	  Query query = em.createQuery("select od.item from Order o join o.orderDetails od "
	      + "where o.transaction.transactionId = :transactionId")
          .setParameter("transactionId", transactionId);
	  
		return query.getResultList();
  }

	@Override
  public List<CouponItem> findByTransactionIdAndStatus(Long transactionId,
      TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.transactionId = :transactionId and pr.transaction.status = :status")
//				.setParameter("transactionId", transactionId)
//				.setParameter("status", status.name());
	  Query query = em.createQuery("select od.item from Order o join o.orderDetails od "
        + "where o.transaction.transactionId = :transactionId and o.transaction.status = :status")
          .setParameter("transactionId", transactionId)
          .setParameter("status", status.name());
		return query.getResultList();
  }

	@Override
  public Order findById(Long orderId) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Order where id = :id")
		  .setParameter("id", orderId);
		return (Order) query.getSingleResult();
  }

	@Override
  public List<CouponItem> findByAccountAndCouponId(Long couponId, Long accountId, OrderStatus status) {
		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.buyer.accountId = :accountId and pr.coupon.couponId = :couponId")
		Query query = em.createQuery("select od.item from Order o join o.orderDetails od "
		      + "where o.transaction.buyer.accountId = :accountId and od.item.coupon.couponId = :couponId and o.status = :status")   
				  .setParameter("accountId", accountId)
				  .setParameter("couponId", couponId)
				  .setParameter("status", status.name());
		
		return query.getResultList();
  }

  @Override
  public CouponItem findCouponItemByOrderAndCouponId(long orderId, long couponId) {
    EntityManager em = entityManagerProvider.get();
    Query query = em.createQuery("select od.item from Order o join o.orderDetails od "
        + "where o.id = :orderId and od.item.coupon.couponId = :couponId")
          .setParameter("orderId", orderId)
          .setParameter("couponId", couponId);
    
    return (CouponItem) query.getSingleResult();
  }
}
