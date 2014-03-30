package com.ziplly.app.dao;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Session;

public class SessionDAOImpl extends BaseDAO implements SessionDAO {
	private Logger logger = Logger.getLogger(SessionDAOImpl.class.getCanonicalName());

	@Inject
	public SessionDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public Session findSessionByUid(Long uid) throws NotFoundException {
		if (uid == null) {
			logger.log(Level.SEVERE, "Invalid argument to findSessionByUid");
			throw new IllegalArgumentException("Invalid argument to findSessionByUid");
		}

		Query query = getEntityManager().createNamedQuery("fetchSessionByUid");
		query.setParameter("uid", uid);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
			session.getAccount().getAccountId();
			Hibernate.initialize(session.getAccount());
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}

		return session;
	}

	@Override
	public Session findSessionByAccountId(Long accountId) throws NotFoundException {
		if (accountId == null) {
			logger.log(Level.SEVERE, "Invalid argument to findSessionByAccountId");
			throw new IllegalArgumentException("Invalid argument to findSessionByAccountId");
		}

		Query query = getEntityManager().createNamedQuery("fetchSessionByAccountId");
		query.setParameter("account_id", accountId);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}

		return session;
	}

	@Transactional
	@Override
	public void save(Session session) {
		if (session == null) {
			logger.log(Level.SEVERE, "Invalid argument to save");
			throw new IllegalArgumentException("Invalid argument to save");
		}
		
		Session existingSession = null;
		try {
			existingSession = findSessionByAccountId(session.getAccount().getAccountId());
		} catch (NotFoundException e) {
		}

		if (existingSession != null) {
			logger.log(Level.INFO, "Duplicate session for user:" + session.getAccount().getAccountId());

			// update it
			existingSession.setTimeCreated(session.getTimeCreated());
			existingSession.setExpireAt(session.getExpireAt());
			existingSession.setUid(session.getUid());
			existingSession.setLocation(session.getLocation());
			getEntityManager().merge(existingSession);
			return;
			// removeByAccountId(session.getAccount().getAccountId());
		}

		getEntityManager().persist(session);
	}

	@Transactional
	@Override
	public void removeByAccountId(Long accountId) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Session session = null;
		try {
			Query query = em.createNamedQuery("fetchSessionByAccountId");
			query.setParameter("account_id", accountId);
			session = (Session) query.getSingleResult();
			em.remove(session);
		} catch (NoResultException nre) {
			// do nothing.
			logger.warning(String.format("Couldn't find session with account %d", accountId));
		}
	}

	@Transactional
	@Override
	public void removeByUid(Long uid) throws NotFoundException {
		if (uid == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("fetchSessionByUid");
		query.setParameter("uid", uid);
		Session session = null;
		try {
			session = (Session) query.getSingleResult();
			em.remove(session);
		} catch (NoResultException nre) {
			logger.warning(String.format("Couldn't find session with uid %d", uid));
			throw new NotFoundException();
		}
	}
}
