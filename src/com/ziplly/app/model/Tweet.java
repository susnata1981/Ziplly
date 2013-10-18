package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(
		name = "findTweetsByZip",
		query = "from Tweet t where t.sender.zip = :zip"),
	@NamedQuery(
		name = "findTweetsByAccountId",
		query = "from Tweet t where t.sender.accountId = :accountId")
})
@Entity
@Table(name="tweet")
public class Tweet implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="tweet_id")
	private long tweetId;
	@ManyToOne(fetch = FetchType.LAZY)
	
	@JoinColumn(name="sender_id")
	private Account sender;

	@Column(name="image_id")
	private long imageId;
	private TweetType type;
	private String content;
	
	@ManyToMany(mappedBy="tweets")
	private Set<TagDTO> tags;
	
	@OneToMany(mappedBy="tweet")
	private List<CommentDTO> comments = new ArrayList<CommentDTO>();
	
	private int status;
	private Date timeCreated;
	
	public Tweet() {
	}
	
	public Tweet(TweetDTO tweet) {
		tweetId = tweet.getTweetId();
		sender = new Account(tweet.getSender());
		imageId = tweet.getImageId();
		type = tweet.getType();
		content = tweet.getContent();
		status = tweet.getStatus();
		timeCreated = tweet.getTimeCreated();
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
	public Date getTime_created() {
		return getTimeCreated();
	}
	public void setTime_created(Date time_created) {
		this.setTimeCreated(time_created);
	}
	public List<CommentDTO> getComments() {
		return comments;
	}
	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}
	public Account getSender() {
		return sender;
	}
	public void setSender(Account sender) {
		this.sender = sender;
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	public long getTweetId() {
		return tweetId;
	}

	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
}
