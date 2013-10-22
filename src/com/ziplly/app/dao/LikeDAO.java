package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.Love;

public interface LikeDAO {
	void save(Love like) throws DuplicateException;
	void delete(Love like);
	Love findLikeByTweetAndAccountId(Long tweetId, Long accountId);
}
