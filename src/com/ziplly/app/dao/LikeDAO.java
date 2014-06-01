package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.server.model.jpa.Love;

public interface LikeDAO {
	LoveDTO save(Love like) throws DuplicateException;

	void delete(Love like);

	LoveDTO findLikeByTweetAndAccountId(Long tweetId, Long accountId);

	Long findLikeCountByAccoutId(Long accountId);
}
