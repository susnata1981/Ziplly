package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetType;

public interface TweetDAO {
	void save(Tweet tweet);
	List<Tweet> findTweetsByAccountId(Long accountId);
	List<Tweet> findTweetsByZip(Integer zip);
	List<Tweet> findTweetsByTypeAndZip(TweetType type, Integer zip);
	Tweet update(Tweet tweet);
	void delete(Tweet tweet);
}
