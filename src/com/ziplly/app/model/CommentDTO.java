package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CommentDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int comment_id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tweet_id")
	private TweetDTO tweet;
	
	private AccountDTO author;
	private String content;
	private Date timeCreated;
	
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
}
