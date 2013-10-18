package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ziplly.app.model.Tweet;

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

}
