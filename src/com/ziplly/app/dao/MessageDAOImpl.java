package com.ziplly.app.dao;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.Message;

public class MessageDAOImpl extends BaseDAO implements MessageDAO {

	@Inject
	public MessageDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(Message msg) {
		if (msg == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(msg);
		em.getTransaction().commit();
	}
}
