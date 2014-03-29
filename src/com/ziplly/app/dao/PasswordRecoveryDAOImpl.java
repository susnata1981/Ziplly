package com.ziplly.app.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.PasswordRecovery;
import com.ziplly.app.model.PasswordRecoveryStatus;

public class PasswordRecoveryDAOImpl extends BaseDAO implements PasswordRecoveryDAO {

	@Inject
	public PasswordRecoveryDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	// TODO: move business logic out of this method.
	@Override
	public void save(PasswordRecovery pr) {
		if (pr == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();

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
		Query query = getEntityManager().createNamedQuery("findPasswordRecoverByHash");
		query.setParameter("hash", hash);
		query.setParameter("status", PasswordRecoveryStatus.PENDING.name());
		return (PasswordRecovery) query.getSingleResult();
	}

	@Override
	public PasswordRecovery findByEmail(String email) {
		Query query = getEntityManager().createNamedQuery("findPasswordRecoverByEmail");
		query.setParameter("email", email);
		query.setParameter("status", PasswordRecoveryStatus.PENDING.name());
		return (PasswordRecovery) query.getSingleResult();
	}

	@Override
	public void update(PasswordRecovery pr) {
		Preconditions.checkNotNull(pr);

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.merge(pr);
		em.getTransaction().commit();
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
