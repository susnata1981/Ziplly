package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.Comment;

public class CommentDAOImpl extends BaseDAO implements CommentDAO {

	@Inject
	public CommentDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public void save(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		em.persist(comment);
		em.getTransaction().commit();
	}

	@Override
	public void delete(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(comment);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Long findCommentCountByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		Query query =
		    getEntityManager().createQuery(
		        "select count(*) from Comment where author.accountId = :accountId");
		query.setParameter("accountId", accountId);

		Long count = (Long) query.getSingleResult();
		return count;
	}

	@Override
	public void update(Comment comment) {
		Preconditions.checkArgument(comment != null);
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(comment);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			throw new IllegalArgumentException();
		}
	}
}
