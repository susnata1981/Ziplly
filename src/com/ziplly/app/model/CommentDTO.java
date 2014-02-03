package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class CommentDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long commentId;
	private TweetDTO tweet;
	private AccountDTO author;
	private String content;
	private Date timeUpdated;
	private Date timeCreated;
	
	public CommentDTO() {
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	public AccountDTO getAuthor() {
		return author;
	}
	public void setAuthor(AccountDTO author) {
		this.author = author;
	}
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public TweetDTO getTweet() {
		return tweet;
	}
	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
}
