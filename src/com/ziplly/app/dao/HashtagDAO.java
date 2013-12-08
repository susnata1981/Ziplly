package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;

public interface HashtagDAO {
	void create(Hashtag hashtag);
	HashtagDTO findByName(String name);
	List<HashtagDTO> findAll();
	List<HashtagDTO> findTopHashtag(int n);
}
