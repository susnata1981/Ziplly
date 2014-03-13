package com.ziplly.app.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.ziplly.app.model.PasswordRecovery;
import com.ziplly.app.model.PasswordRecoveryStatus;

public class PasswordRecoveryDAOImpl implements PasswordRecoveryDAO {

	// TODO: move business logic out of this method.
	@Override
	public void save(PasswordRecovery pr) {
		if (pr == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
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
		} finally {
			em.close();
		}
	}

	@Override
	public PasswordRecovery findByHash(String hash) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createNamedQuery("findPasswordRecoverByHash");
			query.setParameter("hash", hash);
			query.setParameter("status", PasswordRecoveryStatus.PENDING.name());
			return (PasswordRecovery) query.getSingleResult();
		} finally {
			em.close();
		}
	}

	@Override
	public PasswordRecovery findByEmail(String email) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createNamedQuery("findPasswordRecoverByEmail");
			query.setParameter("email", email);
			query.setParameter("status", PasswordRecoveryStatus.PENDING.name());
			return (PasswordRecovery) query.getSingleResult();
		} finally {
			em.close();
		}
	}

	@Override
	public void update(PasswordRecovery pr) {
		Preconditions.checkNotNull(pr);

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(pr);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public void createOrUpdate(String email, String hash) {
		boolean foundPasswordRecoveryLink = false;
		try {
			PasswordRecovery pr = findByEmail(email);
			pr.setHash(hash);
			foundPasswordRecoveryLink = true;
			update(pr);
		} catch (NoResultException nre) {
		}

		// First time create the entry
		if (!foundPasswordRecoveryLink) {
			PasswordRecovery pr = new PasswordRecovery();
			pr.setEmail(email);
			pr.setHash(hash);
			pr.setTimeCreated(new Date());
			pr.setStatus(PasswordRecoveryStatus.PENDING);
			save(pr);
		}
	}
}
