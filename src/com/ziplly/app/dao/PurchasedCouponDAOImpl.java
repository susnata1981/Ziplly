package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;

public class PurchasedCouponDAOImpl extends BaseDAO implements PurchasedCouponDAO {
	private Logger logger = Logger.getLogger(TransactionDAOImpl.class.getName());

	@Inject
	public PurchasedCouponDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	@Transactional
	public void save(PurchasedCoupon pr) {
		entityManagerProvider.get().persist(pr);
	}

	@Override
	@Transactional
	public PurchasedCoupon update(PurchasedCoupon pr) {
		return entityManagerProvider.get().merge(pr);
	}
	
	@Override
	public List<PurchasedCoupon> findTransactionByCouponId(Long couponId, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		Query query =
		    em
		        .createQuery("from PurchasedCoupon pr where pr.coupon.couponId = :couponId and status in (:status)")
		        .setParameter("couponId", couponId)
		        .setParameter("status", ImmutableList.of(PurchasedCouponStatus.UNUSED.name(), PurchasedCouponStatus.USED.name()))
		        .setFirstResult(start)
		        .setMaxResults(pageSize);

		return query.getResultList();
	}
	
	@Override
	public Long getTotalCountByByCouponId(Long couponId) {
		EntityManager em = entityManagerProvider.get();
		Query query =
		    em.createQuery("select count(pr) from PurchasedCoupon pr where pr.coupon.couponId = :couponId and status in (:status)")
		        .setParameter("couponId", couponId)
						.setParameter("status", ImmutableList.of(PurchasedCouponStatus.UNUSED.name(), PurchasedCouponStatus.USED.name()));
		return (Long) query.getSingleResult();
	}
	
	@Override
  public List<PurchasedCoupon> findByAccountIdAndStatus(Long accountId, TransactionStatus status, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.buyer.accountId = :accountId and pr.transaction.status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name())
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<PurchasedCoupon> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public Long getTotalByAccountIdAndStatus(Long accountId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(pr) from PurchasedCoupon pr where pr.transaction.buyer.accountId = :accountId and pr.transaction.status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name());
		
		return (Long) query.getSingleResult();
  }

	@Override
  public List<PurchasedCoupon> findByTransactionId(Long transactionId) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.transactionId = :transactionId")
				.setParameter("transactionId", transactionId);
		
		return query.getResultList();
  }

	@Override
  public List<PurchasedCoupon> findByTransactionIdAndStatus(Long transactionId,
      TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.transactionId = :transactionId and pr.transaction.status = :status")
				.setParameter("transactionId", transactionId)
				.setParameter("status", status.name());
		
		return query.getResultList();
  }

	@Override
  public PurchasedCoupon findById(Long purchaseCouponId) {
		return entityManagerProvider.get().find(PurchasedCoupon.class, purchaseCouponId);
  }

	@Override
  public List<PurchasedCoupon> findByAccountAndCouponId(Long couponId, Long accountId) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from PurchasedCoupon pr where pr.transaction.buyer.accountId = :accountId and pr.coupon.couponId = :couponId")
				.setParameter("accountId", accountId)
				.setParameter("couponId", couponId);
		
		return query.getResultList();
  }
}
