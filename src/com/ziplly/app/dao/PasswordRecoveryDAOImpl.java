package com.ziplly.app.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.model.PasswordRecovery;
import com.ziplly.app.model.PasswordRecoveryStatus;

public class PasswordRecoveryDAOImpl implements PasswordRecoveryDAO {

	@Override
	public void save(PasswordRecovery pr) {
		if (pr == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		boolean found = true;
		PasswordRecovery resp = null;
		try {
			resp = findByEmail(pr.getEmail());
		} catch (NoResultException nre) {
			found = false;
		}

		// if we already have a non-used row, just update it
		if (found && resp.getStatus() != PasswordRecoveryStatus.DONE) {
			resp.setHash(pr.getHash());
			resp.setTimeCreated(new Date());
			em.getTransaction().begin();
			em.merge(resp);
			em.getTransaction().commit();
			return;
		}

		em.getTransaction().begin();
		em.persist(pr);
		em.getTransaction().commit();
	}

	@Override
	public PasswordRecovery findByHash(String hash) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findPasswordRecoverByHash");
		query.setParameter("hash", hash);
		query.setParameter("status", PasswordRecoveryStatus.PENDING);
		return (PasswordRecovery) query.getSingleResult();
	}

	@Override
	public PasswordRecovery findByEmail(String email) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("findPasswordRecoverByEmail");
		query.setParameter("email", email);
		query.setParameter("status", PasswordRecoveryStatus.PENDING);
		return (PasswordRecovery) query.getSingleResult();
	}

	@Override
	public void update(PasswordRecovery pr) {
		if (pr == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.merge(pr);
		em.getTransaction().commit();
	}
}
