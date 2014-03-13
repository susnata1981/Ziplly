package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;

public class SubscriptionPlanDAOImpl implements SubscriptionPlanDAO {

	@Override
	public void save(SubscriptionPlan plan) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(plan);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public SubscriptionPlan get(Long planId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			SubscriptionPlan plan = em.find(SubscriptionPlan.class, planId);
			return plan;
		} finally {
			em.close();
		}
	}

	@Override
	public List<SubscriptionPlanDTO> getAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<SubscriptionPlan> plans =
			    (List<SubscriptionPlan>) em.createQuery("from SubscriptionPlan").getResultList();
			return EntityUtil.cloneSubscriptionPlanList(plans);
		} finally {
			em.close();
		}
	}
}
