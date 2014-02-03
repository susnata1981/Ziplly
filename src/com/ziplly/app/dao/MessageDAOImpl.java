package com.ziplly.app.dao;

import javax.persistence.EntityManager;

import com.ziplly.app.model.Message;

public class MessageDAOImpl implements MessageDAO {

	@Override
	public void save(Message msg) {
		if (msg == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(msg);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
}
