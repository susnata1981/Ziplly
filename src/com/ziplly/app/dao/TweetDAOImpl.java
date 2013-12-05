package com.ziplly.app.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ziplly.app.client.view.StringConstants;
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
	public TweetDTO save(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		em.persist(tweet);

		// save hashtags
		List<String> hashtags = extractHashtags(tweet.getContent());
		for (String hashtag : hashtags) {
			HashtagDTO existingHashtag = null;
			try {
				existingHashtag = hashtagDao.findByName(hashtag);
				tweet.addHashtag(new Hashtag(existingHashtag));
			} catch (NoResultException nre) {
				Hashtag h = new Hashtag();
				h.setTag(hashtag);
				h.addTweet(tweet);
				tweet.addHashtag(h);
			}
		}
		TweetDTO result = EntityUtil.clone(tweet);

		em.getTransaction().commit();
		em.close();
		return result;
	}

	private List<String> extractHashtags(String content) {
		if (content != null) {
			List<String> result = Lists.newArrayList();
			for (String word : content.split("\\s+")) {
				if (word.startsWith(StringConstants.HASHTAG_PREFIX)) {
					result.add(word);
				}
			}
			return result;
		}
		return ImmutableList.of();
	}

	@Override
	public List<TweetDTO> findTweetsByZip(Integer zip, int page, int pageSize) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByZip");
		query.setParameter("zip", zip);
		query.setParameter("status", TweetStatus.ACTIVE);
		query.setFirstResult(page * pageSize).setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>) query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}

	@Override
	public List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip, int page, int pageSize) {
		if (zip == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByTypeAndZip");
		query.setParameter("zip", zip);
		query.setParameter("type", type);
		query.setParameter("status", TweetStatus.ACTIVE);
		query.setFirstResult(page * pageSize).setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>) query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}

	@Override
	public List<TweetDTO> findTweetsByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = (Query) em.createNamedQuery("findTweetsByAccountId");
		query.setParameter("accountId", accountId);
		query.setParameter("status", TweetStatus.ACTIVE);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>) query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
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
			em.close();
			return EntityUtil.clone(result);
		} catch (NoResultException nre) {
			em.close();
			throw nre;
		}
		// em.merge(tweet);
		// em.getTransaction().commit();
		// TweetDTO result = EntityUtil.clone(tweet, false);
		// em.close();
		// return result;
	}

	@Override
	public void delete(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
		query.setParameter("tweetId", tweet.getTweetId());
		Tweet result = (Tweet) query.getSingleResult();
		result.setStatus(TweetStatus.DELETED);
		em.merge(result);
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public List<TweetDTO> findTweetsByAccountId(Long accountId, int page, int pageSize) {
		if (accountId == null || pageSize == 0) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = (Query) em
				.createQuery("from Tweet t where t.sender.accountId = :accountId and status = :status order by t.timeCreated desc");
		query.setParameter("accountId", accountId);
		query.setParameter("status", TweetStatus.ACTIVE);
		query.setFirstResult(page * pageSize).setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>) query.getResultList();
		em.close();
		return EntityUtil.cloneList(tweets);
	}

	@Override
	public Long findTweetsByAccountIdAndMonth(Long accountId, Date date) throws ParseException {
		if (accountId == null || date == null) {
			throw new IllegalArgumentException("Invalid accountId");
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and month(t.timeCreated) = :month and year(timeCreated) = :year and status = :status");
		query.setParameter("accountId", accountId);
		query.setParameter("status", TweetStatus.ACTIVE);
		// +1 to account for mysql format indexed at 1
		query.setParameter("month", cal.get(Calendar.MONTH) + 1);
		query.setParameter("year", cal.get(Calendar.YEAR));
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}

	@Override
	public Long findTweetsCountByAccountId(Long accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em
				.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and status = :status");
		query.setParameter("accountId", accountId);
		query.setParameter("status", TweetStatus.ACTIVE);
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}

	@Override
	public Tweet findTweetById(Long tweetId) {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
		query.setParameter("tweetId", tweetId);
		Tweet result = (Tweet) query.getSingleResult();
		em.close();
		return result;
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
		Query query = em.createQuery("from Tweet order by timeCreated desc");
		@SuppressWarnings("unchecked")
		List<Tweet> result = query.getResultList();
		em.close();
		return EntityUtil.cloneList(result);
	}

	@Override
	public List<TweetDTO> findTweets(String queryStr, int start, int end) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(queryStr);
		query.setFirstResult(start);
		query.setMaxResults(end - start);
		@SuppressWarnings("unchecked")
		List<Tweet> result = query.getResultList();
		List<TweetDTO> resp = EntityUtil.cloneList(result);
		em.close();
		return resp;
	}

	@Override
	public Long findTotalTweetCount(String queryStr) {
		EntityManager em = EntityManagerService.getInstance().getEntityManager();
		Query query = em.createQuery(queryStr);
		Long count = (Long) query.getSingleResult();
		em.close();
		return count;
	}
}
