package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.server.model.jpa.Hashtag;

public interface HashtagDAO {
	void create(Hashtag hashtag);

	HashtagDTO findByName(String name) throws NotFoundException;

	List<HashtagDTO> findAll() throws NotFoundException;

	// List<HashtagDTO> findTopHashtag(int n) throws NotFoundException;
	List<HashtagDTO>
	    findTopHashtagForNeighborhood(Long neighborhoodId, int n) throws NotFoundException;

	List<TweetDTO> getTweetsForTag(String tag, int page, int pageSize) throws NotFoundException;

	/*
	 * Retrieves tweet list based on neighborhood and hashtag
	 */
	List<TweetDTO> getTweetsForTagAndNeighborhood(String hashtag,
	    Long neighborhoodId,
	    int page,
	    int pageSize) throws NotFoundException;

}