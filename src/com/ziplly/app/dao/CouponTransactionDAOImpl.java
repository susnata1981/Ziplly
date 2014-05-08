package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

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

	private Logger logger = Logger.getLogger(CouponTransactionDAOImpl.class.getName());
	
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
  public List<CouponTransaction> findAllCouponTransactionByAccountId(
  		Long accountId, int start, int pageSize) {
		Preconditions.checkNotNull(accountId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId")
				.setParameter("accountId", accountId)
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<CouponTransaction> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public List<CouponTransaction> findByAccountIdAndStatus(Long accountId, TransactionStatus status,
		  int start, int pageSize) {
		Preconditions.checkNotNull(accountId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name())
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<CouponTransaction> coupons = query.getResultList();
		return coupons;
  }
	
	@Override
  public Long findCountByAccountId(Long accountId) {
		Preconditions.checkNotNull(accountId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(distinct c.transactionId) from CouponTransaction c "
				+ "where buyer.accountId = :accountId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.COMPLETE.name());
		
		Long count = (Long) query.getSingleResult();
		return count;
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
  public List<CouponTransaction> findByAccountAndCouponId(Long couponId,
      Long accountId) {
		
		logger.info(String.format("AccountId %d, CouponId %d", accountId, couponId));
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where buyer.accountId = :accountId "
				+ "and coupon.couponId = :couponId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.COMPLETE.name())
				.setParameter("couponId", couponId);
		
		@SuppressWarnings("unchecked")
    List<CouponTransaction> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(c) from CouponTransaction c where buyer.accountId = :accountId "
				+ "and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name());
		
		return (Long) query.getSingleResult();
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
  public CouponTransaction findByIdAndStatus(Long transactionId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction where transactionId = :transactionId and status = :status")
				.setParameter("transactionId", transactionId)
				.setParameter("status", status.name());
		
		return (CouponTransaction) query.getSingleResult();
	}

	@Override
  public List<CouponTransaction> findCouponTransactionByCouponId(Long couponId, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from CouponTransaction c where c.coupon.couponId = :couponId")
				.setParameter("couponId", couponId)
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		return query.getResultList();
  }

	@Override
  public Long getTotalCountByByCouponId(Long couponId) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(c) from CouponTransaction c where c.coupon.couponId = :couponId")
				.setParameter("couponId", couponId);
		
		return (Long) query.getSingleResult();
  }
}
