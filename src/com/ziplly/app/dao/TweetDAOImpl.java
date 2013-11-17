package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public class TweetDAOImpl implements TweetDAO {

	@Override
	public TweetDTO save(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		em.getTransaction().begin();
		em.persist(tweet);
		TweetDTO result = EntityUtil.clone(tweet);
		em.getTransaction().commit();
		em.close();
		return result;
	}

	@Override
	public List<TweetDTO> findTweetsByZip(Integer zip) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByZip");
		query.setParameter("zip", zip);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>)query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}

	@Override
	public List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByTypeAndZip");
		query.setParameter("zip", zip);
		query.setParameter("type", type);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>)query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}
	
	@Override
	public List<TweetDTO> findTweetsByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByAccountId");
		query.setParameter("accountId", accountId);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>)query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}

	@Override
	public TweetDTO update(Tweet tweet) {
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
			em.close();
			return EntityUtil.clone(result);
		} catch(NoResultException nre) {
			em.close();
			throw nre;
		}
	}

	@Override
	public void delete(Tweet tweet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TweetDTO> findTweetsByAccountId(Long accountId, int page,
			int pageSize) {
		if (accountId == null || pageSize == 0) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance()
				.getEntityManager();
		Query query = (Query) em.createQuery("from Tweet t where t.sender.accountId = :accountId order by t.timeCreated desc");
		query.setParameter("accountId", accountId);
		query.setFirstResult(page*pageSize).setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>)query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}
}
