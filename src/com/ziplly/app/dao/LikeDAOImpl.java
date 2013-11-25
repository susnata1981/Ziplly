package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Love;
import com.ziplly.app.model.LoveDTO;

public class LikeDAOImpl implements LikeDAO {

	@Override
	public LoveDTO findLikeByTweetAndAccountId(Long tweetId, Long accountId) {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNamedQuery("findLikeByTweetAndUserId");
		query.setParameter("tweetId", tweetId);
		query.setParameter("accountId", accountId);
		Love result = (Love) query.getSingleResult();
		return EntityUtil.clone(result);
	}
	
	@Override
	public LoveDTO save(Love like) throws DuplicateException {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		
		if (like.getTweet() != null) {
			try {
				findLikeByTweetAndAccountId(like.getTweet().getTweetId(), like.getAuthor().getAccountId());
				throw new DuplicateException();
			} catch(NoResultException nre) {
				// ignore it
			}
		} 
		// else check the Comment

		em.getTransaction().begin();
		em.persist(like);
		em.getTransaction().commit();
		return EntityUtil.clone(like);
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

	@Override
	public Long findLikeCountByAccoutId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("select count(*) from Love where author.accountId = :accountId");
		query.setParameter("accountId", accountId);
		Long count = (Long) query.getSingleResult();
		return count;
	}
}
