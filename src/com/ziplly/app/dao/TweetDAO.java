package com.ziplly.app.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface TweetDAO {

  /**
   * Saves tweet.
   */
  TweetDTO save(Tweet tweet);

	TweetDTO update(Tweet tweet) throws NotFoundException;

	void delete(Tweet tweet) throws NotFoundException;

	List<TweetDTO>
	    findTweetsByAccountId(Long accountId, int page, int pageSize) throws NotFoundException;

	Long findTweetsByAccountIdAndMonth(Long accountId, Date date);

	Long findTweetCountByAccountId(Long accountId) throws NotFoundException;

	Long findTotalCouponsByAccountIdAndMonth(Long accountId, Date date);
	
	TweetDTO findTweetById(Long tweetId) throws NotFoundException;

	List<TweetDTO> findAll();

	Map<TweetType, Integer> findTweetCategoryCounts(Long neighborhoodId) throws NotFoundException;

	/**
	 * Retrieves all tweets based on Neighborhood
	 */
	List<TweetDTO>
	    findTweetsByNeighborhood(Long neighborhoodId, int page, int pageSize);

	/**
	 * Retrieves specific tweet type (except coupons) based 
	 * on Neighborhood
	 */
	List<TweetDTO> findTweetsByTypeAndNeighborhood(TweetType type,
	    Long neighborhoodId,
	    int page,
	    int pageSize) throws NotFoundException;

	/**
   * Special case for type/neighborhood as for coupons we're going to pull any
   * coupons that are published one level up. This could change once we've more
   * traffic and there's no need to pull that data.
   */
  List<TweetDTO> findCouponsByNeighborhood(Long neighborhoodId, int page, int pageSize);
  
	Long findTotalCouponsPublishedBetween(Long accountId, Date before, Date now);

	Long findTotalTweetsPublishedBetween(Long accountId, Date before, Date now);

  List<Tweet> findAllCoupons(int page, int pageSize);
}
