package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Tweet;

public interface TweetDAO {
	void save(Tweet tweet);
	List<Tweet> findTweetsByAccountId(Long accountId);
	List<Tweet> findTweetsByZip(Integer zip);
}
