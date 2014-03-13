package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class SpamDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private AccountDTO reporter;
	private TweetDTO tweet;
	private Date timeCreated;
	private Date timeUpdated;

	public SpamDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountDTO getReporter() {
		return reporter;
	}

	public void setReporter(AccountDTO reporter) {
		this.reporter = reporter;
	}

	public TweetDTO getTweet() {
		return tweet;
	}

	public void setTweet(TweetDTO tweet) {
		this.tweet = tweet;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
}
