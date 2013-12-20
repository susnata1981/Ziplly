package com.ziplly.app.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface TweetDAO {
	TweetDTO save(Tweet tweet);
	List<TweetDTO> findTweetsByAccountId(Long accountId);
	List<TweetDTO> findTweetsByZip(Integer zip, int page, int pageSize);
	List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip,
			int page, int pageSize);
	TweetDTO update(Tweet tweet);
	void delete(Tweet tweet);
	List<TweetDTO> findTweetsByAccountId(Long accountId, int page, int pageSize);
	Long findTweetsByAccountIdAndMonth(Long accountId, Date date) throws ParseException;
	Long findTweetsCountByAccountId(Long accountId);
	Tweet findTweetById(Long tweetId);
	List<TweetDTO> findAll();
	List<TweetDTO> findTweets(String query, int start, int end);
	Long findTotalTweetCount(String queryStr);
	Map<TweetType, Integer> findTweetCategoryCounts(Account acct);
}
