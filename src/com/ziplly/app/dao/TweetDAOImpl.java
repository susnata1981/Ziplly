package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetType;

public class TweetDAOImpl implements TweetDAO {

	@Override
	public void save(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.persist(tweet);
		em.getTransaction().commit();
	}

	@Override
	public List<Tweet> findTweetsByZip(Integer zip) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByZip");
		query.setParameter("zip", zip);
		return query.getResultList();
	}

	@Override
	public List<Tweet> findTweetsByTypeAndZip(TweetType type, Integer zip) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByTypeAndZip");
		query.setParameter("zip", zip);
		query.setParameter("type", type);
		return query.getResultList();
	}
	
	@Override
	public List<Tweet> findTweetsByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByAccountId");
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	@Override
	public Tweet update(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		
		em.getTransaction().begin();
		Query query = (Query) em.createNamedQuery("findTweetsById");
		query.setParameter("tweetId", tweet.getTweetId());
		try {
			Tweet result = (Tweet) query.getSingleResult();
			result.setContent(tweet.getContent());
			result.setType(tweet.getType());
			em.merge(result);
			em.getTransaction().commit();
			return result;
		} catch(NoResultException nre) {
			throw nre;
		}
	}

	@Override
	public void delete(Tweet tweet) {
		// TODO Auto-generated method stub
		
	}
}
