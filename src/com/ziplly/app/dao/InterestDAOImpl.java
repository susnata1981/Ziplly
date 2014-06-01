package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.server.model.jpa.Interest;

public class InterestDAOImpl extends BaseDAO implements InterestDAO {

	@Inject
	public InterestDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public Interest findInterestByName(String name) {
		EntityManager em = getEntityManager();
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
	public List<InterestDTO> findAll() {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("from Interest");
		try {
			@SuppressWarnings("unchecked")
			List<Interest> result = query.getResultList();
			return EntityUtil.cloneInterestList(result);
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public void save(Interest interest) {
		if (interest == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Interest existingInterest = findInterestByName(interest.getName());
		if (existingInterest != null) {
			// throw new IllegalArgumentException("Duplicate entry");
			return;
		}
		em.persist(interest);
		em.getTransaction().commit();
	}
}
