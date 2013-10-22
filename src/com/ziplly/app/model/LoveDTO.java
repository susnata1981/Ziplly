package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class LoveDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long likeId;
	TweetDTO tweet;
	CommentDTO comment;
	AccountDTO author;
	Date timeCreated;
	
	public LoveDTO() {
	}
	
	public LoveDTO(Love like) {
		this.likeId = like.getLikeId();
//		if (like.getComment() != null) {
//			this.comment = new CommentDTO(like.getComment());
//		}
//		if (like.getTweet() != null) {
//			this.tweet = new TweetDTO(like.getTweet());
//		}
		this.author = AccountHandlerUtil.getAccountDTO(like.getAuthor());
		this.timeCreated = like.getTimeCreated();
	}
	
	public TweetDTO getTweet() {
		return tweet;
	}
	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
	public CommentDTO getComment() {
		return comment;
	}
	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}
	public AccountDTO getAuthor() {
		return author;
	}
	public void setAuthor(AccountDTO author) {
		this.author = author;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Long getLikeId() {
		return likeId;
	}

	public void setLikeId(Long likeId) {
		this.likeId = likeId;
	}
}
