package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Love;

public class LikeDAOImpl implements LikeDAO {

	@Override
	public Love findLikeByTweetAndAccountId(Long tweetId, Long accountId) {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findLikeByTweetAndUserId");
		query.setParameter("tweetId", tweetId);
		query.setParameter("accountId", accountId);
		Love result = (Love) query.getSingleResult();
		return result;
	}
	
	@Override
	public void save(Love like) throws DuplicateException {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		
		if (like.getTweet() != null) {
			try {
				Love result = findLikeByTweetAndAccountId(like.getTweet().getTweetId(), like.getAuthor().getAccountId());
				throw new DuplicateException();
			} catch(NoResultException nre) {
				// ignore it
			}
		} 
		// else check the Comment

		em.getTransaction().begin();
		em.persist(like);
		em.getTransaction().commit();
	}

	@Override
	public void delete(Love like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.remove(like);
		em.getTransaction().commit();
	}
}
