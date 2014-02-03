package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.ziplly.app.model.Comment;

public class CommentDAOImpl implements CommentDAO {

	@Override
	public void save(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(comment);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public void delete(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(comment);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			throw new IllegalArgumentException();
		} finally {
			em.close();
		}
	}

	@Override
	public Long findCommentCountByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		Query query = em
				.createQuery("select count(*) from Comment where author.accountId = :accountId");
		query.setParameter("accountId", accountId);

		try {
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}

	@Override
	public void update(Comment comment) {
		Preconditions.checkArgument(comment != null);
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(comment);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			throw new IllegalArgumentException();
		} finally {
			em.close();
		}
	}
}
