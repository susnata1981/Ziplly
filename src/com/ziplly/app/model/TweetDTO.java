package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long tweetId;
	private AccountDTO sender;
	private long imageId;
	private TweetType type;
	private String content;
	private List<CommentDTO> comments = new ArrayList<CommentDTO>();
	private List<LoveDTO> likes = new ArrayList<LoveDTO>();
	private int status;
	private Date timeCreated;
	
	public TweetDTO() {
	}
	
	public TweetType getType() {
		return type;
	}
	public void setType(TweetType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<CommentDTO> getComments() {
		return comments;
	}
	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}
	public AccountDTO getSender() {
		return sender;
	}
	public void setSender(AccountDTO sender) {
		this.sender = sender;
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	public Long getTweetId() {
		return tweetId;
	}
	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public void setLikes(List<LoveDTO> likes) {
		this.likes = likes;
	}
	
	public List<LoveDTO> getLikes() {
		return likes;
	}
}
