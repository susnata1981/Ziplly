package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.server.model.jpa.Coupon;

public class CouponDAOImpl implements CouponDAO {

	private Provider<EntityManager> entityManagerProvider;

	@Inject
	public CouponDAOImpl(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
  }
	
	@Override
  public Coupon findById(Long couponId) {
		EntityManager em = entityManagerProvider.get();
		return em.find(Coupon.class, couponId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Coupon> findCouponsByAccountId(Long accountId, int start, int pageSize) {
		EntityManager em = entityManagerProvider.get();
		
		Query query = em.createQuery("select c from Coupon c, Tweet t where c.tweet.tweetId = t.tweetId "
				+ "and c.tweet.sender.accountId = :accountId order by c.timeCreated desc")
		    .setParameter("accountId", accountId)
		    .setFirstResult(start)
		    .setMaxResults(pageSize);

		List<Coupon> coupons = query.getResultList();
		return coupons;
  }

	@Override
  public Long getTotalCouponCountByAccountId(Long accountId) {
		EntityManager em = entityManagerProvider.get();
		
		Query query = em.createQuery("select count(c) from Coupon c, Tweet t where c.tweet.tweetId = t.tweetId and c.tweet.sender.accountId = :accountId")
		    .setParameter("accountId", accountId);
		
		return (Long) query.getSingleResult();
  }
	
}
