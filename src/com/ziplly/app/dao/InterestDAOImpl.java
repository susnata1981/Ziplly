package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.model.Interest;

public class InterestDAOImpl implements InterestDAO {

	@Override
	public Interest findInterestByName(String name) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findInterestByName");
		query.setParameter("name", name);
		try {
			Interest interest = (Interest) query.getSingleResult();
			return interest;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public void save(Interest interest) {
		if (interest == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Interest existingInterest = findInterestByName(interest.getName());
		if (existingInterest != null) {
			//throw new IllegalArgumentException("Duplicate entry");
			return;
		}
		
		em.persist(interest);
		em.getTransaction().commit();
	}

}
