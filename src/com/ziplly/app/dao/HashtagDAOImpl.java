package com.ziplly.app.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.model.jpa.Hashtag;

public class HashtagDAOImpl extends BaseDAO implements HashtagDAO {

	private TweetDAO tweetDao;

	@Inject
	public HashtagDAOImpl(Provider<EntityManager> entityManagerFactory, TweetDAO tweetDao) {
		super(entityManagerFactory);
		this.tweetDao = tweetDao;
	}

	@Transactional
	@Override
	public void create(Hashtag hashtag) {
		if (hashtag == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		em.persist(hashtag);
		em.flush();
	}

	@Transactional
	@Override
	public HashtagDTO findByName(String name) throws NotFoundException {
		if (name == null) {
			throw new IllegalArgumentException("Invalid argument");
		}
		
		EntityManager em = getEntityManager();
		try {
			Query query = em.createQuery("from Hashtag where tag = :name");
			query.setParameter("name", name);
			Hashtag result = (Hashtag) query.getSingleResult();
			HashtagDTO resp = EntityUtil.clone(result);
			return resp;
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}
	}

	@Override
	public List<HashtagDTO> findAll() throws NotFoundException {
		EntityManager em = getEntityManager();
		try {
			Query query = em.createQuery("from Hashtag");
			@SuppressWarnings("unchecked")
			List<Hashtag> result = query.getResultList();
			return EntityUtil.cloneHashtahList(result);
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}
	}

	@Transactional
	@Override
	public List<HashtagDTO>
	    findTopHashtagForNeighborhood(Long neighborhoodId, int maxResult) throws NotFoundException {
		EntityManager em = getEntityManager();

		try {
			Query query =
			    em
			        .createNativeQuery("select h.id, h.tag, h.time_created from hashtag h where h.id in"
			            + "(select max(id) from tweet_hashtag where tweet_id in "
			            + "(select t.tweet_id from tweet t, tweet_neighborhood tn where t.tweet_id = tn.tweet_id and tn.neighborhood_id = :neighborhoodId) "
			            + "group by id order by count(tweet_id))");

			query.setParameter("neighborhoodId", neighborhoodId).setMaxResults(maxResult);

			@SuppressWarnings("unchecked")
			List<Object> result = query.getResultList();
			List<HashtagDTO> response = Lists.newArrayList();
			for (Object o : result) {
				Object[] r = (Object[]) o;
				// System.out.println(r[0] + ":" + r[1] + ":" + r[2]);
				HashtagDTO h = new HashtagDTO();
				BigInteger hId = (BigInteger) r[0];
				h.setId(hId.longValue());
				h.setTag((String) r[1]);
				response.add(h);
			}

			return response;
		} catch (NoResultException nre) {
			throw new NotFoundException();
		}
	}

	@Transactional
	@Override
	public List<TweetDTO>
	    getTweetsForTag(String hashtag, int page, int pageSize) throws NotFoundException {

		if (hashtag == null) {
			throw new IllegalArgumentException("Invalid argument");
		}

		EntityManager em = getEntityManager();
		List<TweetDTO> tweets = Lists.newArrayList();
		try {
			HashtagDTO result = findByName(hashtag);
			int start = page * pageSize;
			Query query =
			    em
			        .createNativeQuery("select tweet_id from tweet_hashtag where id = :id")
			        .setParameter("id", result.getId())
			        .setFirstResult(start)
			        .setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<BigInteger> tweetIds = query.getResultList();
			for (BigInteger tweetId : tweetIds) {
				TweetDTO tweet = tweetDao.findTweetById(tweetId.longValue());
				tweets.add(tweet);
			}
		} catch (NoResultException nre) {
			throw nre;
		}

		return tweets;
	}

	@Override
	public List<TweetDTO> getTweetsForTagAndNeighborhood(String hashtag,
	    Long neighborhoodId,
	    int page,
	    int pageSize) throws NotFoundException {
		if (hashtag == null) {
			throw new IllegalArgumentException("Invalid argument");
		}

		EntityManager em = getEntityManager();
		List<TweetDTO> tweets = Lists.newArrayList();
		try {
			HashtagDTO result = findByName(hashtag);
			int start = page * pageSize;
			Query query =
			    em
			        .createNativeQuery("select tweet_id from tweet_hashtag where id = :id")
			        .setParameter("id", result.getId())
			        .setFirstResult(start)
			        .setMaxResults(pageSize);

			for (Object tweetId : query.getResultList()) {
				BigInteger id = (BigInteger) tweetId;
				TweetDTO tweet = tweetDao.findTweetById(id.longValue());
				// if (tweet.getSender().getNeighborhood().getNeighborhoodId() ==
				// neighborhoodId) {
				// tweets.add(tweet);
				// }
				if (containsNeighborhood(tweet.getSender(), neighborhoodId)) {
					tweets.add(tweet);
				}
			}
		} catch (NotFoundException e) {
			throw e;
		}

		return tweets;
	}

	public boolean containsNeighborhood(AccountDTO account, Long neighborhoodId) {
		for (LocationDTO loc : account.getLocations()) {
			if (loc.getNeighborhood().getNeighborhoodId() == neighborhoodId) {
				return true;
			}
		}

		return false;
	}
}
