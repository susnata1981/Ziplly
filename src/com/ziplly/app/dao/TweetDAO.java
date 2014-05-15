package com.ziplly.app.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface TweetDAO {
	TweetDTO save(Tweet tweet);

	/*
	 * Should be removed eventually, as we call the paginated version of this api.
	 */
	List<TweetDTO> findAllTweetsByAccountId(Long accountId) throws NotFoundException;

	TweetDTO update(Tweet tweet) throws NotFoundException;

	void delete(Tweet tweet) throws NotFoundException;

	/*
	 * Paginated version of findTweetsByAccountId
	 */
	List<TweetDTO>
	    findTweetsByAccountId(Long accountId, int page, int pageSize) throws NotFoundException;

	Long findTweetsByAccountIdAndMonth(Long accountId, Date date);

	Long findTweetsCountByAccountId(Long accountId) throws NotFoundException;

	Long findTotalCouponsByAccountIdAndMonth(Long accountId, Date date);
	
	TweetDTO findTweetById(Long tweetId) throws NotFoundException;

	List<TweetDTO> findAll();

	List<TweetDTO> findTweets(String query, int start, int end);

	Long findTotalTweetCount(String queryStr);

	Map<TweetType, Integer> findTweetCategoryCounts(Long neighborhoodId) throws NotFoundException;

	/*
	 * Retrieves all tweets based on Neighborhood
	 */
	List<TweetDTO>
	    findTweetsByNeighborhood(Long neighborhoodId, int page, int pageSize) throws NotFoundException;

	/*
	 * Retrieves specific tweet type based on Neighborhood
	 */
	List<TweetDTO> findTweetsByTypeAndNeighborhood(TweetType type,
	    Long neighborhoodId,
	    int page,
	    int pageSize) throws NotFoundException;

	Long findTotalCouponsPublishedBetween(Long accountId, Date before, Date now);

	Long findTotalTweetsPublishedBetween(Long accountId, Date before, Date now);
}
