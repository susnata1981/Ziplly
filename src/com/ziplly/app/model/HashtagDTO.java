package com.ziplly.app.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class HashtagDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String tag;
	private Set<TweetDTO> tweets = new HashSet<TweetDTO>();

	public Set<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(Set<TweetDTO> tweets) {
		this.tweets = tweets;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void add(TweetDTO t) {
		tweets.add(t);
	}
}
