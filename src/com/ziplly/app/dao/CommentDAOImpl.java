package com.ziplly.app.dao;

import javax.persistence.EntityManager;

import com.ziplly.app.model.Comment;

public class CommentDAOImpl implements CommentDAO {

	@Override
	public void save(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.persist(comment);
		em.getTransaction().commit();
	}

	@Override
	public void delete(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.remove(comment);
		em.getTransaction().commit();
	}
}
