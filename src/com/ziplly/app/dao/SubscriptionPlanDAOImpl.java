package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;

public class SubscriptionPlanDAOImpl extends BaseDAO implements SubscriptionPlanDAO {

	@Inject
	public SubscriptionPlanDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(SubscriptionPlan plan) {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(plan);
		em.getTransaction().commit();

	}

	@Override
	public SubscriptionPlan get(Long planId) {
		SubscriptionPlan plan = getEntityManager().find(SubscriptionPlan.class, planId);
		return plan;
	}

	@Override
	public List<SubscriptionPlanDTO> getAll() {
		@SuppressWarnings("unchecked")
		List<SubscriptionPlan> plans =
		    (List<SubscriptionPlan>) getEntityManager()
		        .createQuery("from SubscriptionPlan")
		        .getResultList();
		return EntityUtil.cloneSubscriptionPlanList(plans);
	}
}
