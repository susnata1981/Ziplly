package com.ziplly.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.server.model.jpa.Love;

public class LikeDAOImpl extends BaseDAO implements LikeDAO {

	@Inject
	public LikeDAOImpl(Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider);
	}

	@Override
	public LoveDTO findLikeByTweetAndAccountId(Long tweetId, Long accountId) {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}

		Query query = getEntityManager().createNamedQuery("findLikeByTweetAndUserId");
		query.setParameter("tweetId", tweetId);
		query.setParameter("accountId", accountId);
		Love result = (Love) query.getSingleResult();
		return EntityUtil.clone(result);
	}

	@Transactional
	@Override
	public LoveDTO save(Love like) throws DuplicateException {
		if (like == null) {
			throw new IllegalArgumentException();
		}

		if (like.getTweet() != null) {
			try {
				findLikeByTweetAndAccountId(like.getTweet().getTweetId(), like.getAuthor().getAccountId());
				throw new DuplicateException();
			} catch (NoResultException nre) {
				// ignore it
			}
		}

		// else check the Comment
		EntityManager em = getEntityManager();
		em.persist(like);
		return EntityUtil.clone(like);
	}

	@Transactional
	@Override
	public void delete(Love like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.remove(like);
	}

	@Override
	public Long findLikeCountByAccoutId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		Query query =
		    getEntityManager().createQuery(
		        "select count(*) from Love where author.accountId = :accountId");
		query.setParameter("accountId", accountId);
		Long count = (Long) query.getSingleResult();
		return count;
	}
}
