package com.ziplly.app.dao;

import java.text.ParseException;
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
	List<TweetDTO> findAllTweetsByAccountId(Long accountId);
	
	List<TweetDTO> findTweetsByZip(Integer zip, int page, int pageSize);
	
	List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip,
			int page, int pageSize);
	
	TweetDTO update(Tweet tweet);
	
	void delete(Tweet tweet);
	
	/*
	 * Paginated version of findTweetsByAccountId
	 */
	List<TweetDTO> findTweetsByAccountId(Long accountId, int page, int pageSize);
	
	Long findTweetsByAccountIdAndMonth(Long accountId, Date date) throws ParseException;
	Long findTweetsCountByAccountId(Long accountId);
	TweetDTO findTweetById(Long tweetId) throws NotFoundException;
	List<TweetDTO> findAll();
	List<TweetDTO> findTweets(String query, int start, int end);
	
	Long findTotalTweetCount(String queryStr);
	
//	Map<TweetType, Integer> findTweetCategoryCounts(Account acct);
	Map<TweetType, Integer> findTweetCategoryCounts(Long neighborhoodId);
	
	/*
	 * Retrieves all tweets based on Neighborhood
	 */
	List<TweetDTO> findTweetsByNeighborhood(Long neighborhoodId, int page, int pageSize) throws NotFoundException;
	

	/*
	 * Retrieves specific tweet type based on Neighborhood
	 */
	List<TweetDTO> findTweetsByTypeAndNeighborhood(TweetType type, Long neighborhoodId, int page,
			int pageSize);
}
