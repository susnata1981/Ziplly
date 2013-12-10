package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.TweetDTO;

public interface HashtagDAO {
	void create(Hashtag hashtag);
	HashtagDTO findByName(String name);
	List<HashtagDTO> findAll();
	List<HashtagDTO> findTopHashtag(int n);
	List<TweetDTO> getTweetsForTag(String tag, int page, int pageSize);
}
