package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

	@Override
	public Long findCommentCountByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();		
		
	Query query = em.createQuery("select count(*) from Comment where author.accountId = :accountId");
	query.setParameter("accountId", accountId);
	Long count = (Long) query.getSingleResult();
	return count;
	}
}
