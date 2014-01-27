package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.TweetDTO;

public class HashtagDAOImpl implements HashtagDAO {

	private TweetDAO tweetDao;

	@Inject
	public HashtagDAOImpl(TweetDAO tweetDao) {
		this.tweetDao = tweetDao;
	}
	
	@Override
	public void create(Hashtag hashtag) {
		if (hashtag == null) {
			throw new IllegalArgumentException();
		}
		
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(hashtag);
		em.getTransaction().commit();
		em.flush();
		em.close();
	}

	@Override
	public HashtagDTO findByName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Hashtag where tag = :name");
		query.setParameter("name", name);
		Hashtag result = (Hashtag) query.getSingleResult();
		em.getTransaction().commit();
		HashtagDTO resp = EntityUtil.clone(result);
		em.close();
		return resp;
	}

	@Override
	public List<HashtagDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Hashtag");
		@SuppressWarnings("unchecked")
		List<Hashtag> result = query.getResultList();
		em.close();
		return EntityUtil.cloneHashtahList(result);
	}

	@Override
	public List<HashtagDTO> findTopHashtag(int n) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createNativeQuery("select h.id,h.tag,h.time_created from hashtag h where h.id in (select max(id) "
				+ "from tweet_hashtag group by id order by count(tweet_id))");
		@SuppressWarnings("unchecked")
		List<Object> result = query.getResultList();
		List<HashtagDTO> response = Lists.newArrayList();
		for(Object o : result) {
			Object [] r = (Object[]) o;
			System.out.println(r[0]+":"+r[1]+":"+r[2]);
			HashtagDTO h = new HashtagDTO();
			BigInteger hId = (BigInteger) r[0];
			h.setId(hId.longValue());
			h.setTag((String) r[1]);
			response.add(h);
		}
		
		em.close();
		return response;
	}

	@Override
	public List<TweetDTO> getTweetsForTag(String hashtag, int page, int pageSize) {
		if (hashtag == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		List<TweetDTO> tweets = Lists.newArrayList();
		try {
			HashtagDTO result = findByName(hashtag);
			int start = page * pageSize;
			Query query = em.createNativeQuery("select tweet_id from tweet_hashtag where id = :id")
				.setParameter("id", result.getId())
				.setFirstResult(start)
				.setMaxResults(pageSize);
			
			@SuppressWarnings("unchecked")
			List<BigInteger> tweetIds = query.getResultList();
			for(BigInteger tweetId : tweetIds) {
				TweetDTO tweet = tweetDao.findTweetById(tweetId.longValue());
				tweets.add(tweet);
			}
		} catch (NoResultException nre) {
			throw nre;
		} finally {
			em.close();
		}
		
		return tweets;	
	}
	
	@Override
	public List<TweetDTO> getTweetsForTagAndNeighborhood(String hashtag, Long neighborhoodId, int page, int pageSize) {
		if (hashtag == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		List<TweetDTO> tweets = Lists.newArrayList();
		try {
			HashtagDTO result = findByName(hashtag);
			int start = page * pageSize;
			Query query = em.createNativeQuery("select tweet_id from tweet_hashtag where id = :id")
				.setParameter("id", result.getId())
				.setFirstResult(start)
				.setMaxResults(pageSize);
			
			for(Object tweetId : query.getResultList()) {
				BigInteger id = (BigInteger)tweetId;
				TweetDTO tweet = tweetDao.findTweetById(id.longValue());
				if (tweet.getSender().getNeighborhood().getNeighborhoodId() == neighborhoodId) {
					tweets.add(tweet);
				}
			}
		} catch (NoResultException nre) {
			throw nre;
		} finally {
			em.close();
		}
		return tweets;	
	}
}
