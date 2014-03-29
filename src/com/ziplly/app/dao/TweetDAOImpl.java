package com.ziplly.app.dao;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.Image;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;

public class TweetDAOImpl extends BaseDAO implements TweetDAO {
	private Logger logger = Logger.getLogger(TweetDAOImpl.class.getCanonicalName());

	@Inject
	public TweetDAOImpl(Provider<EntityManager> entityManagerFactory) {
		super(entityManagerFactory);
	}

	@Override
	public TweetDTO save(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		logger.info(String.format("About to save tweet %s", tweet.getContent()));

		EntityManager em = getEntityManager();
			em.getTransaction().begin();

			// save hashtags
			Set<String> hashtags = extractHashtags(tweet.getContent());
			Set<Hashtag> existingTags = new HashSet<Hashtag>();
			for (String hashtag : hashtags) {
				try {
					Query query =
					    em.createQuery("from Hashtag where tag = :name").setParameter("name", hashtag);
					Hashtag result = (Hashtag) query.getSingleResult();
					existingTags.add(result);
				} catch (NoResultException nre) {
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

			// load images
			Set<Image> images = new HashSet<Image>();
			for (Image image : tweet.getImages()) {
				try {
					Query query =
					    em.createQuery("from Image where id = :id").setParameter("id", image.getId());
					Image existingImage = (Image) query.getSingleResult();
					images.add(existingImage);
				} catch (NoResultException ex) {
					// do nothing.
					logger.severe(String.format("Failed to load image with id %d", image.getId()));
				}
			}

			if (images.size() > 0) {
				tweet.setImages(images);
			}

			em.persist(tweet);
			em.getTransaction().commit();
			TweetDTO result = EntityUtil.clone(tweet);
			return result;
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

	@Override
	public List<TweetDTO>
	    findTweetsByNeighborhood(Long neighborhoodId, int page, int pageSize) throws NotFoundException {

		if (neighborhoodId == null) {
			throw new IllegalArgumentException();
		}

		try {
			Query query = (Query) getEntityManager().createNamedQuery("findTweetsByNeighborhood");
			query.setParameter("neighborhoodId", neighborhoodId);
			query.setParameter("status", TweetStatus.ACTIVE.name());
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for neighborhoodId %d, exception %s",
			    neighborhoodId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public List<TweetDTO> findTweetsByTypeAndNeighborhood(TweetType type,
	    Long neighborhoodId,
	    int page,
	    int pageSize) throws NotFoundException {
		Preconditions.checkArgument(neighborhoodId != null);

		try {
			Query query = (Query) getEntityManager().createNamedQuery("findTweetsByTypeAndNeighborhood");
			query.setParameter("neighborhoodId", neighborhoodId);
			query.setParameter("type", type.name());
			query.setParameter("status", TweetStatus.ACTIVE.name());
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);

			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			logger.info(String.format(
			    "Retrieved %d tweets for type %s, neighborhoodId %d ",
			    tweets.size(),
			    type.name(),
			    neighborhoodId));
			return EntityUtil.cloneList(tweets);
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for type %s, neighborhoodId %d, exception %s",
			    type.name(),
			    neighborhoodId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public List<TweetDTO> findAllTweetsByAccountId(Long accountId) throws NotFoundException {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		try {
			Query query = (Query) getEntityManager().createNamedQuery("findTweetsByAccountId");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE.name());
			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for accountId %d exception %s",
			    accountId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public TweetDTO update(Tweet tweet) throws NotFoundException {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}
		EntityManager em = getEntityManager();

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
			logger.warning(String.format(
			    "Failed to update tweet id=[%d], exception %s",
			    tweet.getTweetId(),
			    nre));
			throw new NotFoundException();
		} catch(RuntimeException re) {
			em.getTransaction().rollback();
			return null;
		}
	}

	@Override
	public void delete(Tweet tweet) throws NotFoundException {
		if (tweet == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
			query.setParameter("tweetId", tweet.getTweetId());
			Tweet result = (Tweet) query.getSingleResult();
			result.setStatus(TweetStatus.DELETED);
			em.merge(result);
			em.getTransaction().commit();
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets with id [%d] exception %s",
			    tweet.getTweetId(),
			    nre));
			throw new NotFoundException();
		} catch(RuntimeException re) {
			em.getTransaction().rollback();
		}
	}

	@Override
	public List<TweetDTO>
	    findTweetsByAccountId(Long accountId, int page, int pageSize) throws NotFoundException {
		if (accountId == null || pageSize == 0) {
			throw new IllegalArgumentException();
		}

		try {
			Query query =
			    (Query) getEntityManager().createQuery("from Tweet t where t.sender.accountId = :accountId and status = :status order by t.timeCreated desc");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE.name());
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Tweet> tweets = (List<Tweet>) query.getResultList();
			return EntityUtil.cloneList(tweets);
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for accountId %d exception %s",
			    accountId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public Long findTweetsByAccountIdAndMonth(Long accountId, Date date) throws ParseException,
	    NotFoundException {
		
		if (accountId == null || date == null) {
			throw new IllegalArgumentException("Invalid accountId");
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		EntityManager em = getEntityManager();
		try {
			Query query =
			    em.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and month(t.timeCreated) = :month and year(timeCreated) = :year and status = :status");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE.name());
			// +1 to account for mysql format indexed at 1
			query.setParameter("month", cal.get(Calendar.MONTH) + 1);
			query.setParameter("year", cal.get(Calendar.YEAR));
			Long count = (Long) query.getSingleResult();
			return count;
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for accountId %d exception %s",
			    accountId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public Long findTweetsCountByAccountId(Long accountId) throws NotFoundException {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}

		try {
			Query query =
			    getEntityManager().createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and status = :status");
			query.setParameter("accountId", accountId);
			query.setParameter("status", TweetStatus.ACTIVE.name());
			Long count = (Long) query.getSingleResult();
			return count;
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweets for accountId %d exception %s",
			    accountId,
			    nre));
			throw new NotFoundException();
		} 
	}

	@Override
	public TweetDTO findTweetById(Long tweetId) throws NotFoundException {
		if (tweetId == null) {
			throw new IllegalArgumentException();
		}

		EntityManager em = getEntityManager();
		try {
			Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
			query.setParameter("tweetId", tweetId);
			Tweet result = (Tweet) query.getSingleResult();
			return EntityUtil.clone(result);
		} catch (NoResultException nre) {
			logger.warning(String.format(
			    "Failed to retrieve tweet with id[%d] exception %s",
			    tweetId,
			    nre));
			throw new NotFoundException();
		}
	}

	@Override
	public List<TweetDTO> findAll() {
		EntityManager em = getEntityManager();
			Query query = em.createQuery("from Tweet order by timeCreated desc");
			@SuppressWarnings("unchecked")
			List<Tweet> result = query.getResultList();
			return EntityUtil.cloneList(result);
	}

	@Deprecated
	@Override
	public List<TweetDTO> findTweets(String queryStr, int start, int end) {
		EntityManager em = getEntityManager();
			Query query = em.createQuery(queryStr);
			query.setFirstResult(start);
			query.setMaxResults(end - start);
			@SuppressWarnings("unchecked")
			List<Tweet> result = query.getResultList();
			List<TweetDTO> resp = EntityUtil.cloneList(result);
			return resp;
	}

	@Override
	public Long findTotalTweetCount(String queryStr) {
		EntityManager em = getEntityManager();
			Query query = em.createQuery(queryStr);
			Long count = (Long) query.getSingleResult();
			return count;
	}

	/*
	 * Finds counts for different tweet types
	 */
	@Override
	public Map<TweetType, Integer>
	    findTweetCategoryCounts(Long neighborhoodId) throws NotFoundException {
		
		if (neighborhoodId == null) {
			throw new IllegalArgumentException("Invalid argument to findTweetCategoryCounts");
		}

		Map<TweetType, Integer> result = Maps.newHashMap();
		EntityManager em = getEntityManager();

		try {
			Query query =
			    em.createNativeQuery(
			            "select t.type, count(*) from tweet t, tweet_neighborhood tn "
			                + "where t.tweet_id = tn.tweet_id and tn.neighborhood_id = :neighborhood_id group by t.type")
			        .setParameter("neighborhood_id", neighborhoodId);

			@SuppressWarnings("rawtypes")
			List resultList = query.getResultList();
			for (Object r : resultList) {
				Object[] temp = (Object[]) r;
				result.put(TweetType.valueOf((String) temp[0]), ((BigInteger) temp[1]).intValue());
			}
			return result;
		} catch (NoResultException nre) {
			logger.info(String.format(
			    "Failed to retrieve tweets for neighborhoodId [%d] exception %s",
			    neighborhoodId,
			    nre));
			throw new NotFoundException();
		} 
	}
	
	public static void main(String[] args) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		System.out.println("M=" + (cal.get(Calendar.MONTH) + 1));
		System.out.println("Y=" + cal.get(Calendar.YEAR));
	}
}
