package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.TransactionStatus;

public class CouponTransactionDAOImpl extends BaseDAO implements CouponTransactionDAO {

	@Inject
	public CouponTransactionDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}
	
	@Override
	@Transactional
  public void save(CouponTransaction couponTransaction) {
		Preconditions.checkNotNull(couponTransaction);
		EntityManager em = entityManagerProvider.get();
		em.persist(couponTransaction);
  }

	@Override
	@Transactional
  public CouponTransaction update(CouponTransaction couponTransaction) {
		Preconditions.checkNotNull(couponTransaction);
		EntityManager em = entityManagerProvider.get();
		return em.merge(couponTransaction);
  }
	
	@Override
  public List<CouponTransaction> findCouponTransactionByAccountId(Long accountId) {
		Preconditions.checkNotNull(accountId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.ACTIVE.name());
		
		@SuppressWarnings("unchecked")
    List<CouponTransaction> coupons = query.getResultList();
		return coupons;
  }

	@Transactional
	@Override
  public Coupon findByCouponId(Long couponId) {
		Preconditions.checkNotNull(couponId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Coupon where couponId = :couponId")
				.setParameter("couponId", couponId);
		return (Coupon) query.getSingleResult();
  }
	
	@Transactional
	@Override
  public CouponTransaction findCouponTransactionByIdAndStatus(long transactionId, TransactionStatus status) {
		Preconditions.checkNotNull(transactionId);
		Preconditions.checkNotNull(status);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where transactionId = :transactionId and status = :status")
				.setParameter("transactionId", transactionId)
				.setParameter("status", status.name());
		
		return (CouponTransaction) query.getSingleResult();
	}
}
