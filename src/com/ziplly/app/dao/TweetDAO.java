package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public interface TweetDAO {
	TweetDTO save(Tweet tweet);
	List<TweetDTO> findTweetsByAccountId(Long accountId);
	List<TweetDTO> findTweetsByZip(Integer zip);
	List<TweetDTO> findTweetsByTypeAndZip(TweetType type, Integer zip);
	TweetDTO update(Tweet tweet);
	void delete(Tweet tweet);
}
