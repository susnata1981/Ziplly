package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.ziplly.app.model.SubscriptionPlan;

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
	public List<SubscriptionPlan> getAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		return (List<SubscriptionPlan>)em.createQuery("from SubscriptionPlan").getResultList();
	}

}
