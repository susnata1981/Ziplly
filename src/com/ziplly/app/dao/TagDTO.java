package com.ziplly.app.dao;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class TagDTO {
	private String keyword;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="tag_tweet", joinColumns= {
		@JoinColumn(name="tweet_id") }, 
		inverseJoinColumns= {
				@JoinColumn(name="tag_id")
		})
	private List<TweetDTO> tweets;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
