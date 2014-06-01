package com.ziplly.app.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.model.jpa.Transaction;

public class TransactionDAOImpl extends BaseDAO implements TransactionDAO {

	private Logger logger = Logger.getLogger(TransactionDAOImpl.class.getName());
	
	@Inject
	public TransactionDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}
	
	@Override
	@Transactional
  public void save(Transaction Transaction) {
		Preconditions.checkNotNull(Transaction);
		EntityManager em = entityManagerProvider.get();
		em.persist(Transaction);
  }

	@Override
	@Transactional
  public Transaction update(Transaction Transaction) {
		Preconditions.checkNotNull(Transaction);
		EntityManager em = entityManagerProvider.get();
		return em.merge(Transaction);
  }
	
	@Override
  public List<Transaction> findAllTransactionByAccountId(Long accountId, int start, int pageSize) {
		Preconditions.checkNotNull(accountId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Transaction where buyer.accountId = :accountId")
				.setParameter("accountId", accountId)
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<Transaction> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public List<Transaction> findByAccountIdAndStatus(Long accountId, TransactionStatus status,
		  int start, int pageSize) {
		Preconditions.checkNotNull(accountId);
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Transaction where buyer.accountId = :accountId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name())
				.setFirstResult(start)
				.setMaxResults(pageSize);
		
		@SuppressWarnings("unchecked")
    List<Transaction> coupons = query.getResultList();
		return coupons;
  }
	
	@Override
  public Long findCountByAccountId(Long accountId) {
		Preconditions.checkNotNull(accountId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(distinct c.transactionId) from Transaction c "
				+ "where buyer.accountId = :accountId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.COMPLETE.name());
		
		Long count = (Long) query.getSingleResult();
		return count;
  }
	
//	@Transactional
//	@Override
//  public Coupon findByCouponId(Long couponId) {
//		Preconditions.checkNotNull(couponId);
//		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("from Coupon where couponId = :couponId")
//				.setParameter("couponId", couponId);
//		return (Coupon) query.getSingleResult();
//  }

	@Override
  public List<Transaction> findByAccountAndCouponId(Long couponId,
      Long accountId) {
		
		logger.info(String.format("AccountId %d, CouponId %d", accountId, couponId));
		
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Transaction where buyer.accountId = :accountId "
				+ "and coupon.couponId = :couponId and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", TransactionStatus.COMPLETE.name())
				.setParameter("couponId", couponId);
		
		@SuppressWarnings("unchecked")
    List<Transaction> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public Long getTotalCountByAccountIdAndStatus(Long accountId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("select count(c) from Transaction c where buyer.accountId = :accountId "
				+ "and status = :status")
				.setParameter("accountId", accountId)
				.setParameter("status", status.name());
		
		return (Long) query.getSingleResult();
  }
	
	@Override
  public Transaction findById(Long transactionId) {
		Preconditions.checkNotNull(transactionId);
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Transaction where transactionId = :transactionId")
				.setParameter("transactionId", transactionId);
		return (Transaction) query.getSingleResult();
  }
	
	@Transactional
	@Override
  public Transaction findByIdAndStatus(Long transactionId, TransactionStatus status) {
		EntityManager em = entityManagerProvider.get();
		Query query = em.createQuery("from Transaction where transactionId = :transactionId and status = :status")
				.setParameter("transactionId", transactionId)
				.setParameter("status", status.name());
		
		return (Transaction) query.getSingleResult();
	}

	@Override
  public List<Transaction> findAllCouponTransactionByAccountId(Long accountId,
      int start,
      int pageSize) {
	  // TODO Auto-generated method stub
	  return null;
  }

  @Override
  public Transaction findByOrderId(String orderId) {
    EntityManager em = entityManagerProvider.get();
    Query query = em.createQuery("from Transaction where orderId = :orderId")
        .setParameter("orderId", orderId);
    
    return (Transaction) query.getSingleResult();
  }

//	@Override
//  public List<Transaction> findTransactionByCouponId(Long couponId, int start, int pageSize) {
//		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("from Transaction c where c.coupon.couponId = :couponId")
//				.setParameter("couponId", couponId)
//				.setFirstResult(start)
//				.setMaxResults(pageSize);
//		
//		return query.getResultList();
//  }

//	@Override
//  public Long getTotalCountByByCouponId(Long couponId) {
//		EntityManager em = entityManagerProvider.get();
//		Query query = em.createQuery("select count(c) from Transaction c where c.coupon.couponId = :couponId")
//				.setParameter("couponId", couponId);
//		
//		return (Long) query.getSingleResult();
//  }
}
