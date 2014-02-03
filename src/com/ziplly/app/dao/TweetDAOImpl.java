package com.ziplly.app.dao;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;

public class TweetDAOImpl implements TweetDAO {
	private HashtagDAO hashtagDao;

	@Inject
	public TweetDAOImpl(HashtagDAO hashtagDao) {
		this.hashtagDao = hashtagDao;
	}

	@Override
	public TweetDTO save(Tweet tweet)  {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();

			// save hashtags
			Set<String> hashtags = extractHashtags(tweet.getContent());
			Set<Hashtag> existingTags = new HashSet<Hashtag>();
			for (String hashtag : hashtags) {
				HashtagDTO existingHashtag = null;
				try {
					existingHashtag = hashtagDao.findByName(hashtag);
					existingTags.add(new Hashtag(existingHashtag));
				} catch (NotFoundException nre) {
					Hashtag h = new Hashtag();
					h.setTag(hashtag);
					h.addTweet(tweet);
					h.setTimeCreated(new Date());
					em.persist(h);
					existingTags.add(h);
				}
			}

			if (existingTags.size() > 0) {
				tweet.setHashtags(existingTags);
			}

			em.persist(tweet);
			em.getTransaction().commit();
			TweetDTO result = EntityUtil.clone(tweet);
			return result;
		} finally {
			em.close();
		}
	}

	private Set<String> extractHashtags(String content) {
		if (content != null) {
			Set<String> result = Sets.newHashSet();
			for (String word : content.split("\\s+")) {
				if (word.startsWith(StringConstants.HASHTAG_PREFIX)) {
					result.add(word);
				}
			}
			return result;
		}
		return ImmutableSet.of();
	}

	@Deprecated
	@Override
	public List<TweetDTO> findTweetsByZip(Integer zip, int page, int pageSize) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em.createNamedQuery("findTweetsByZip");
			query.setParameter("zip", zip);
			query.setParameter("status", TweetStatus.ACTIVE);
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Override
	public List<TweetDTO> findTweetsByNeighborhood(Long neighborhoodId, int page, int pageSize) {
		if (neighborhoodId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em.createNamedQuery("findTweetsByNeighborhood");
			query.setParameter("neighborhoodId", neighborhoodId);
			query.setParameter("status", TweetStatus.ACTIVE);
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Deprecated
	@Override
	public List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip, int page, int pageSize) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em.createNamedQuery("findTweetsByTypeAndZip");
			query.setParameter("zip", zip);
			query.setParameter("type", type);
			query.setParameter("status", TweetStatus.ACTIVE);
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Override
	public List<TweetDTO> findTweetsByTypeAndNeighborhood(TweetType type, Long neighborhoodId,
			int page, int pageSize) {
		Preconditions.checkArgument(neighborhoodId != null);

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em.createNamedQuery("findTweetsByTypeAndNeighborhood");
			query.setParameter("neighborhoodId", neighborhoodId);
			query.setParameter("type", type);
			query.setParameter("status", TweetStatus.ACTIVE);
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Override
	public List<TweetDTO> findAllTweetsByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em.createNamedQuery("findTweetsByAccountId");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE);
			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Override
	public TweetDTO update(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		em.getTransaction().begin();
		Query query = (Query) em.createNamedQuery("findTweetsById");
		query.setParameter("tweetId", tweet.getTweetId());
		try {
			Tweet result = (Tweet) query.getSingleResult();
			result.setContent(tweet.getContent());
			result.setType(tweet.getType());
			result.setStatus(tweet.getStatus());
			em.merge(result);
			em.getTransaction().commit();
			return EntityUtil.clone(result);
		} catch (NoResultException nre) {
			throw nre;
		} finally {
			em.close();
		}
	}

	@Override
	public void delete(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
			query.setParameter("tweetId", tweet.getTweetId());
			Tweet result = (Tweet) query.getSingleResult();
			result.setStatus(TweetStatus.DELETED);
			em.merge(result);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	@Override
	public List<TweetDTO> findTweetsByAccountId(Long accountId, int page, int pageSize) {
		if (accountId == null || pageSize == 0) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = (Query) em
					.createQuery("from Tweet t where t.sender.accountId = :accountId and status = :status order by t.timeCreated desc");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE);
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} finally {
			em.close();
		}
	}

	@Override
	public Long findTweetsByAccountIdAndMonth(Long accountId, Date date) throws ParseException {
		if (accountId == null || date == null) {
			throw new IllegalArgumentException("Invalid accountId");
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em
					.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and month(t.timeCreated) = :month and year(timeCreated) = :year and status = :status");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE);
			// +1 to account for mysql format indexed at 1
			query.setParameter("month", cal.get(Calendar.MONTH) + 1);
			query.setParameter("year", cal.get(Calendar.YEAR));
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}

	@Override
	public Long findTweetsCountByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em
					.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and status = :status");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE);
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}

	@Override
	public TweetDTO findTweetById(Long tweetId) throws NotFoundException {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
			query.setParameter("tweetId", tweetId);
			Tweet result = (Tweet) query.getSingleResult();
			return EntityUtil.clone(result);
		} catch(NoResultException nre) {
			throw new NotFoundException();
		} finally {
			em.close();
		}
	}

	public static void main(String[] args) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		System.out.println("M=" + (cal.get(Calendar.MONTH) + 1));
		System.out.println("Y=" + cal.get(Calendar.YEAR));
	}

	@Override
	public List<TweetDTO> findAll() {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery("from Tweet order by timeCreated desc");
			@SuppressWarnings("unchecked")
			List<Tweet> result = query.getResultList();
			return EntityUtil.cloneList(result);
		} finally {
			em.close();
		}
	}

	@Override
	public List<TweetDTO> findTweets(String queryStr, int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery(queryStr);
			query.setFirstResult(start);
			query.setMaxResults(end - start);
			@SuppressWarnings("unchecked")
			List<Tweet> result = query.getResultList();
			List<TweetDTO> resp = EntityUtil.cloneList(result);
			return resp;
		} finally {
			em.close();
		}
	}

	@Override
	public Long findTotalTweetCount(String queryStr) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		try {
			Query query = em.createQuery(queryStr);
			Long count = (Long) query.getSingleResult();
			return count;
		} finally {
			em.close();
		}
	}

	/*
	 * Finds the count for different tweet types
	 */
	@Override
	public Map<TweetType, Integer> findTweetCategoryCounts(Account acct) {
		if (acct == null) {
			throw new IllegalArgumentException("Invalid argument to findTweetCategoryCounts");
		}

		Map<TweetType, Integer> result = Maps.newHashMap();
		EntityManager em = EntityManagerService.getInstance().getEntityManager();

		try {
			Query query = em
					.createNativeQuery(
							"select t.type, count(*) from tweet t, account a "
									+ "where t.sender_id = a.account_id and a.neighborhood_id = :neighborhood_id group by t.type")
					.setParameter("neighborhood_id", acct.getNeighborhood().getNeighborhoodId());

			@SuppressWarnings("rawtypes")
			List resultList = query.getResultList();
			for (Object r : resultList) {
				Object[] temp = (Object[]) r;
				result.put(TweetType.values()[(Integer) temp[0]], ((BigInteger) temp[1]).intValue());
			}
			return result;
		} finally {
			em.close();
		}
	}
}
