package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class CommentDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private int commentId;
	private TweetDTO tweet;
	private AccountDTO author;
	private String content;
	private Date timeCreated;
	
	public CommentDTO() {
	}
	
	public CommentDTO(Comment comment) {
		this.commentId = comment.getCommentId();
		this.author = AccountHandlerUtil.getAccountDTO(comment.getAuthor()); 
		this.content = comment.getContent();
		this.timeCreated = comment.getTimeCreated();
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
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public TweetDTO getTweet() {
		return tweet;
	}
	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}
}
