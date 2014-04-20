package com.ziplly.app.dao;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.Coupon;

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
	
}
