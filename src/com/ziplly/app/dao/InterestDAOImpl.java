package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.model.Interest;
import com.ziplly.app.model.InterestDTO;

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
		} finally {
			em.close();
		}

	}

	@Override
	public List<InterestDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Interest");
		try {
			@SuppressWarnings("unchecked")
			List<Interest> result = query.getResultList();
			return EntityUtil.cloneInterestList(result);
		} catch (NoResultException nre) {
			return null;
		} finally {
			em.close();
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
			// throw new IllegalArgumentException("Duplicate entry");
			return;
		}
		try {
			em.persist(interest);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
