package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.shared.SpamStatus;

@Entity
@Table(name="spam")
public class Spam extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@OneToOne
	private Account reporter;
	@OneToOne
	private Tweet tweet;
	@Column(name = "status")
	private SpamStatus status;
	
	public Spam() {
	}

	public Spam(SpamDTO spam) {
		this.reporter = new Account(spam.getReporter());
		this.tweet = new Tweet(spam.getTweet());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getReporter() {
		return reporter;
	}

	public void setReporter(Account reporter) {
		this.reporter = reporter;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	public SpamStatus getStatus() {
		return status;
	}

	public void setStatus(SpamStatus status) {
		this.status = status;
	}
}
