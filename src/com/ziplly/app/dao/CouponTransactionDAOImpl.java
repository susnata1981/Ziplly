package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;

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
  public List<CouponTransaction> findCouponTransactionByAccountId(
  		Long accountId, int start, int pageSize) {
		Preconditions.checkNotNull(accountId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId and status != :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.PENDING.name())
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
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

	@Override
  public Long findCouponTransactionCountByAccountId(Long accountId) {
		Preconditions.checkNotNull(accountId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(distinct c.transactionId) from CouponTransaction c where buyer.accountId = :accountId and status != :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.PENDING.name());
		
		Long count = (Long) query.getSingleResult();
		return count;
  }

	@Override
  public List<CouponTransaction> findCouponTransactionByAccountAndCouponId(
  		Long couponId,
      Long accountId) {
		
		Preconditions.checkNotNull(accountId);
		Preconditions.checkNotNull(couponId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId "
				+ "and coupon.couponId = :couponId and status != :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.PENDING.name())
				.setParameter("couponId", couponId);
		
		@SuppressWarnings("unchecked")
    List<CouponTransaction> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public CouponTransaction findById(Long transactionId) {
		Preconditions.checkNotNull(transactionId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where transactionId = :transactionId")
				.setParameter("transactionId", transactionId);
		return (CouponTransaction) query.getSingleResult();
  }
	
	@Transactional
	@Override
  public CouponTransaction findCouponTransactionByIdAndStatus(Long transactionId, TransactionStatus status) {
		Preconditions.checkNotNull(transactionId);
		Preconditions.checkNotNull(status);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where transactionId = :transactionId and status = :status")
				.setParameter("transactionId", transactionId)
				.setParameter("status", status.name());
		
		return (CouponTransaction) query.getSingleResult();
	}
}
