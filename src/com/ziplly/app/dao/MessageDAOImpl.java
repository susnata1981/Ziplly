package com.ziplly.app.dao;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.server.model.jpa.Message;

public class MessageDAOImpl extends BaseDAO implements MessageDAO {

	@Inject
	public MessageDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Transactional
	@Override
	public void save(Message msg) {
		if (msg == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.persist(msg);
	}
}
