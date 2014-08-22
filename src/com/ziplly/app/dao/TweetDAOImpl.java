package com.ziplly.app.dao;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
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

import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.model.jpa.Hashtag;
import com.ziplly.app.server.model.jpa.Image;
import com.ziplly.app.server.model.jpa.Tweet;

public class TweetDAOImpl extends BaseDAO implements TweetDAO {
  private Logger logger = Logger.getLogger(TweetDAOImpl.class.getCanonicalName());
  private NeighborhoodDAO neighborhoodDao;

  @Inject
  public TweetDAOImpl(Provider<EntityManager> entityManagerFactory, NeighborhoodDAO neighborhoodDao) {
    super(entityManagerFactory);
    this.neighborhoodDao = neighborhoodDao;
  }

  @Transactional
  @Override
  public TweetDTO save(Tweet tweet) {
    if (tweet == null) {
      throw new IllegalArgumentException();
    }

    logger.info(String.format("About to save tweet %s", tweet.getContent()));
    EntityManager em = getEntityManager();

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
        Query query = em.createQuery("from Image where id = :id").setParameter("id", image.getId());
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
    TweetDTO result = EntityUtil.clone(tweet);
    return result;
  }

  /**
   * Find tweets for all neighborhoods that falls under the city which 
   * contains the given neighborhood.
   */
  @Override
  public List<Tweet> findTweetsByNeighborhood(
      long neighborhoodId,
      int page,
      int pageSize) {

    List<Long> neighborhoodIds = neighborhoodDao.getNeigborhoodIdsForCity(neighborhoodId);
    neighborhoodIds.add(neighborhoodId);
    return findTweets(neighborhoodIds, Arrays.asList(TweetType.values()), page, pageSize);
  }

  private List<Tweet> findTweets(
      List<Long> neighborhoodIds,
      List<TweetType> tweetTypes,
      int page,
      int pageSize) {

    try {
      List<String> tweetTypeNames = extractTweetTypeNames(tweetTypes);
      
      Query query =
          (Query) getEntityManager()
              .createQuery(
                  "select t from Tweet t join t.targetNeighborhoods tn "
                      + "where tn.neighborhoodId in (:neigborhoodIds) and t.type in (:tweetTypes)"
                      + " and t.status = :status order by t.timeCreated desc");

      query
          .setParameter("neigborhoodIds", neighborhoodIds)
          .setParameter("tweetTypes", tweetTypeNames)
          .setParameter("status", TweetStatus.ACTIVE.name())
          .setFirstResult(page * pageSize)
          .setMaxResults(pageSize);

      @SuppressWarnings("unchecked")
      List<Tweet> tweets = (List<Tweet>) query.getResultList();
      return tweets;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for neighborhoodId %s, exception %s",
          neighborhoodIds,
          nre));
      return ImmutableList.of();
    }
  }

  @Override
  public List<Tweet> findTweetsByTypeAndNeighborhood(
      TweetType type,
      long neighborhoodId,
      int page,
      int pageSize) throws NotFoundException {

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
      
      return tweets;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for type %s, neighborhoodId %d, exception %s",
          type.name(),
          neighborhoodId,
          nre));
      return ImmutableList.of();
    }
  }

  @Override
  public List<Tweet>
      findCouponsByNeighborhood(Long neighborhoodId, int page, int pageSize) {
 
    if (neighborhoodId == null) {
      throw new IllegalArgumentException();
    }

    try {
      List<Long> neighborhoodIds = neighborhoodDao.getNeigborhoodIdsForCity(neighborhoodId);
      List<String> promotionTypeNames = extractTweetTypeNames(ImmutableList.of(TweetType.COUPON));
      
      Query query =
          (Query) getEntityManager()
              .createQuery(
                  "select t from Tweet t join t.targetNeighborhoods tn "
                      + "where tn.neighborhoodId in (:neigborhoodIds) and t.type in (:tweetTypes)"
                      + " and t.status = :status order by t.timeCreated desc");

      query
          .setParameter("neigborhoodIds", neighborhoodIds)
          .setParameter("tweetTypes", promotionTypeNames)
          .setParameter("status", TweetStatus.ACTIVE.name())
          .setFirstResult(page * pageSize)
          .setMaxResults(pageSize);

      @SuppressWarnings("unchecked")
      List<Tweet> tweets = (List<Tweet>) query.getResultList();
      return tweets;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for neighborhoodId %d, exception %s",
          neighborhoodId,
          nre));
      return ImmutableList.of();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Tweet> findAllCoupons(int page, int pageSize) {
    try {
      List<String> promotionTypeNames = extractTweetTypeNames(ImmutableList.of(TweetType.COUPON));
      Query query =
          (Query) getEntityManager()
              .createQuery(
                  "select t from Tweet t where t.type in (:tweetTypes)"
                      + " and t.status = :status order by t.timeCreated desc");
      query
          .setParameter("tweetTypes", promotionTypeNames)
          .setParameter("status", TweetStatus.ACTIVE.name())
          .setFirstResult(page * pageSize)
          .setMaxResults(pageSize);

      return query.getResultList();
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets with exception %s", nre));
      return ImmutableList.of();
    }
  }
  
  @Transactional
  @Override
  public TweetDTO update(Tweet tweet) throws NotFoundException {
    if (tweet == null) {
      throw new IllegalArgumentException();
    }
    EntityManager em = getEntityManager();

    Query query = (Query) em.createNamedQuery("findTweetsById");
    query.setParameter("tweetId", tweet.getTweetId());
    try {
      Tweet result = (Tweet) query.getSingleResult();
      result.setContent(tweet.getContent());
      result.setType(tweet.getType());
      result.setStatus(tweet.getStatus());
      em.merge(result);
      return EntityUtil.clone(result);
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to update tweet id=[%d], exception %s",
          tweet.getTweetId(),
          nre));
      throw new NotFoundException();
    }
  }

  @Transactional
  @Override
  public void delete(Tweet tweet) throws NotFoundException {
    if (tweet == null) {
      throw new IllegalArgumentException();
    }

    EntityManager em = getEntityManager();
    try {
      Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
      query.setParameter("tweetId", tweet.getTweetId());
      Tweet result = (Tweet) query.getSingleResult();
      result.setStatus(TweetStatus.DELETED);
      em.merge(result);
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets with id [%d] exception %s",
          tweet.getTweetId(),
          nre));
      throw new NotFoundException();
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
          (Query) getEntityManager()
              .createQuery(
                  "from Tweet t where t.sender.accountId = :accountId and status = :status order by t.timeCreated desc");
      
      query.setParameter("accountId", accountId)
        .setParameter("status", TweetStatus.ACTIVE.name())
        .setFirstResult(page * pageSize)
        .setMaxResults(pageSize);
      
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

  /**
   * Returns total number of active tweets for the given month.
   */
  @Override
  public Long findTweetsByAccountIdAndMonth(Long accountId, Date date) {

    DateTime inputDate = new DateTime(date);
    EntityManager em = getEntityManager();
    try {
      Query query =
          em
              .createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId and month(t.timeCreated) = :month "
                  + "and year(timeCreated) = :year and status = :status");
      query.setParameter("accountId", accountId);
      query.setParameter("status", TweetStatus.ACTIVE.name());
      query.setParameter("month", inputDate.getMonthOfYear());
      query.setParameter("year", inputDate.getYear());
      Long count = (Long) query.getSingleResult();
      return count;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for accountId %d exception %s",
          accountId,
          nre));
      return 0L;
    }
  }

  @Deprecated
  @Override
  public Long findTotalCouponsByAccountIdAndMonth(Long accountId, long date) {
    DateTime dateTime = new DateTime(date);
    EntityManager em = getEntityManager();
    try {
      Query query =
          em
              .createQuery("select count(*) from Tweet where sender.accountId = :accountId and month(timeCreated) = :month "
                  + "and year(timeCreated) = :year and status = :status and coupon.couponId is not null");
      query.setParameter("accountId", accountId);
      query.setParameter("status", TweetStatus.ACTIVE.name());
      query.setParameter("month", dateTime.getMonthOfYear());
      query.setParameter("year", dateTime.getYear());
      Long count = (Long) query.getSingleResult();
      return count;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for accountId %d exception %s",
          accountId,
          nre));
      return 0L;
    }
  }

  @Override
  public long findTotalCouponsPublishedBetween(long accountId, Date before, Date now) {
    EntityManager em = getEntityManager();
    try {
      Query query =
          em.createQuery("select count(*) from Tweet where sender.accountId = :accountId "
                  + "and timeCreated between :before and :now and status = :status and coupon.couponId is not null");
      query.setParameter("accountId", accountId);
      query.setParameter("status", TweetStatus.ACTIVE.name());
      query.setParameter("before", before);
      query.setParameter("now", now);
      Long count = (Long) query.getSingleResult();
      return count;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for accountId %d exception %s",
          accountId,
          nre));
      return 0L;
    }
  }

//  @Override
//  public Long findTotalTweetsPublishedBetween(Long accountId, long before, long now) {
//    EntityManager em = getEntityManager();
//    try {
//      Query query =
//          em.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId "
//                  + "and t.creationTime between :before and :now and status = :status and t.coupon.couponId is null");
//      query.setParameter("accountId", accountId);
//      query.setParameter("status", TweetStatus.ACTIVE.name());
//      query.setParameter("before", before);
//      query.setParameter("now", now);
//      Long count = (Long) query.getSingleResult();
//      return count;
//    } catch (NoResultException nre) {
//      logger.warning(String.format(
//          "Failed to retrieve tweets for accountId %d exception %s",
//          accountId,
//          nre));
//      return 0L;
//    }
//  }

  @Override
  public long findTotalTweetsPublishedBetween(long accountId, Date before, Date now) {
    EntityManager em = getEntityManager();
    try {
      Query query =
          em.createQuery("select count(*) from Tweet t where t.sender.accountId = :accountId "
                  + "and t.creationTime between :before and :now and status = :status and t.coupon.couponId is null");
      query.setParameter("accountId", accountId);
      query.setParameter("status", TweetStatus.ACTIVE.name());
      query.setParameter("before", before);
      query.setParameter("now", now);
      Long count = (Long) query.getSingleResult();
      return count;
    } catch (NoResultException nre) {
      logger.warning(String.format(
          "Failed to retrieve tweets for accountId %d exception %s",
          accountId,
          nre));
      return 0L;
    }
  }
  
  @Override
  public Long findTweetCountByAccountId(Long accountId) throws NotFoundException {
    if (accountId == null) {
      throw new IllegalArgumentException();
    }

    try {
      Query query =
          getEntityManager()
              .createQuery(
                  "select count(*) from Tweet t where t.sender.accountId = :accountId and status = :status");
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
  public Tweet findTweetById(Long tweetId) throws NotFoundException {
    if (tweetId == null) {
      throw new IllegalArgumentException();
    }

    EntityManager em = getEntityManager();
    try {
      Query query = em.createQuery("from Tweet t where t.tweetId = :tweetId");
      query.setParameter("tweetId", tweetId);
      return (Tweet) query.getSingleResult();
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

  /**
   * Finds counts for different tweet types
   */
  @Override
  public Map<TweetType, Integer> findTweetCategoryCounts(long neighborhoodId) throws NotFoundException {
    Map<TweetType, Integer> result = Maps.newHashMap();
    EntityManager em = getEntityManager();
    Query query;

    try {
       query =
          em.createNativeQuery(
                  "select t.type, count(distinct(t.tweet_id)) from tweet t, tweet_neighborhood tn "
                      + "where t.tweet_id = tn.tweet_id and tn.neighborhood_id in (:neighborhood_id) group by t.type")
              .setParameter("neighborhood_id", neighborhoodId);

      @SuppressWarnings("rawtypes")
      List resultList = query.getResultList();
      for (Object r : resultList) {
        Object[] temp = (Object[]) r;
        result.put(TweetType.valueOf((String) temp[0]), ((BigInteger) temp[1]).intValue());
      }
      
      // Overwrite it for COUPONS
      result.put(TweetType.COUPON, getTotalCouponCountByNeighborhood(neighborhoodId).intValue());
      result.put(TweetType.ALL, getTotalTweetTypeCount(result));
      return result;
      
    } catch (NoResultException nre) {
      logger.info(String.format(
          "Failed to retrieve tweets for neighborhoodId [%d] exception %s",
          neighborhoodId,
          nre));
      throw new NotFoundException();
    }
  }

  private int getTotalTweetTypeCount(Map<TweetType, Integer> countMap) {
    int total = 0;
    
    for(TweetType type : countMap.keySet()) {
//      System.out.println("T =" + type + " C = "+countMap.get(type));
      if (type == TweetType.ALL) {
        continue;
      }
      
      total += countMap.get(type);
    }
    System.out.println("TOTAL = "+ total);
    return total;
  }

  public Long getTotalCouponCountByNeighborhood(long neighborhoodId) {
    try {
      List<Long> neighborhoodIds = neighborhoodDao.getNeigborhoodIdsForCity(neighborhoodId);
      List<String> promotionTypeNames = extractTweetTypeNames(ImmutableList.of(TweetType.COUPON));
      
      Query query =
          (Query) getEntityManager()
              .createQuery(
                  "select count(distinct t) from Tweet t join t.targetNeighborhoods tn "
                      + "where tn.neighborhoodId in (:neigborhoodIds) and t.type in (:tweetTypes)"
                      + " and t.status = :status order by t.timeCreated desc");

      query
          .setParameter("neigborhoodIds", neighborhoodIds)
          .setParameter("tweetTypes", promotionTypeNames)
          .setParameter("status", TweetStatus.ACTIVE.name());

      return (Long) query.getSingleResult();
    } catch(NoResultException ex) {
      return 0L;
    }
  }
  
  private List<String> extractTweetTypeNames(List<TweetType> promotionTypes) {
    return Lists.transform(promotionTypes, new Function<TweetType, String>() {

      @Override
      public String apply(TweetType type) {
        return type.name();
      }

    });
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

  public static void main(String[] args) throws ParseException {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());

    System.out.println("M=" + (cal.get(Calendar.MONTH) + 1));
    System.out.println("Y=" + cal.get(Calendar.YEAR));
  }
}
