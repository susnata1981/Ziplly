package com.ziplly.app.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Session;

public class SessionDAOImpl implements SessionDAO {
	// private EntityManager em;
	private Logger logger = Logger.getLogger(SessionDAOImpl.class
			.getCanonicalName());

	public SessionDAOImpl() {
	}

	@Override
	public Session findSessionByUid(Long uid) throws NotFoundException {
		if (uid == null) {
			logger.log(Level.SEVERE, "Invalid argument to findSessionByUid");
			throw new IllegalArgumentException(
					"Invalid argument to findSessionByUid");
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("fetchSessionByUid");
		query.setParameter("uid", uid);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}

		return session;
	}

	@Override
	public Session findSessionByAccountId(Long accountId)
			throws NotFoundException {
		if (accountId == null) {
			logger.log(Level.SEVERE,
					"Invalid argument to findSessionByAccountId");
			throw new IllegalArgumentException(
					"Invalid argument to findSessionByAccountId");
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = em.createNamedQuery("fetchSessionByAccountId");
		query.setParameter("account_id", accountId);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}

		return session;
	}

	@Override
	public void save(Session session) {
		if (session == null) {
			logger.log(Level.SEVERE, "Invalid argument to save");
			throw new IllegalArgumentException("Invalid argument to save");
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Session existingSession = null;
		try {
			existingSession = findSessionByAccountId(session.getAccount()
					.getAccountId());
		} catch (NotFoundException e) {
		}

		if (existingSession != null) {
			logger.log(Level.SEVERE, "Duplicate session for user:"
					+ session.getAccount().getAccountId());
			// remove it
			removeByAccountId(session.getAccount().getAccountId());
		}

		em.getTransaction().begin();
		;
		em.persist(session);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void removeByAccountId(Long accountId) {
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Session session = null;
		em.getTransaction().begin();
		try {
			session = findSessionByAccountId(accountId);
		} catch (NotFoundException e) {
			return;
		}

		em.remove(session);
		em.getTransaction().commit();
	}

	@Override
	public void removeByUid(Long uid) throws NotFoundException {
		if (uid == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		
		em.getTransaction().begin();
		Query query = em.createNamedQuery("fetchSessionByUid");
		query.setParameter("uid", uid);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
			em.remove(session);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}
}
