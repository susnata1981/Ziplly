package com.ziplly.app.dao;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CommentDTO {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int comment_id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tweet_id")
	private TweetDTO tweet;
}
