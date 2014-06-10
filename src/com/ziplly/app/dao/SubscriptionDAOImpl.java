package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.server.model.jpa.Subscription;

public class SubscriptionDAOImpl implements SubscriptionDAO {
	private Provider<EntityManager> entityManagerProvider;

	@Inject
	public SubscriptionDAOImpl(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	@Transactional
	@Override
	public void save(Subscription subscription) {
		entityManagerProvider.get().persist(subscription);
	}

	@Override
	public Subscription findById(Long subscriptionId) {
		return entityManagerProvider.get().find(Subscription.class, subscriptionId);
	}

	@Override
	public List<Subscription> findByAccountId(Long accountId) {
		try {
			EntityManager em = entityManagerProvider.get();
			Query query =
			    em
			        .createQuery(
			            "from Subscription s where s.transaction.buyer.accountId = :accountId order by timeCreated desc")
			        .setParameter("accountId", accountId);
			return query.getResultList();
		} catch (NoResultException nre) {
			return ImmutableList.of();
		}
	}
}
