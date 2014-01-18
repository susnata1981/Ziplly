package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;

public class SubscriptionPlanDAOImpl implements SubscriptionPlanDAO {

	@Override
	public void save(SubscriptionPlan plan) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(plan);
		em.getTransaction().commit();
		em.close();
	}
	
	@Override
	public SubscriptionPlan get(Long planId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		SubscriptionPlan plan = em.find(SubscriptionPlan.class, planId);
		return plan;
	}

	@Override
	public List<SubscriptionPlanDTO> getAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		@SuppressWarnings("unchecked")
		List<SubscriptionPlan> plans = (List<SubscriptionPlan>)em.createQuery("from SubscriptionPlan").getResultList();
		return EntityUtil.cloneSubscriptionPlanList(plans);
	}
}
