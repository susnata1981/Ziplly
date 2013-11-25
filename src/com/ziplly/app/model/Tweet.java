package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@NamedQueries({
	@NamedQuery(
		name = "findTweetsByZip",
		query = "from Tweet t where t.sender.zip = :zip and status = :status order by timeCreated desc"),
	@NamedQuery(
		name = "findTweetsById",
		query = "from Tweet t where t.tweetId = :tweetId order by timeCreated desc"),
	@NamedQuery(
		name = "findTweetsByAccountId",
		query = "from Tweet t where t.sender.accountId = :accountId and status = :status order by timeCreated desc"),
	@NamedQuery(
			name = "findTweetsByTypeAndZip",
			query = "from Tweet t where t.sender.zip = :zip and status = :status and t.type = :type order by timeCreated desc"),
})
@Entity
@Table(name="tweet")
public class Tweet extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="tweet_id")
	private long tweetId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="sender_id")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size=10)
	private Account sender;

	@Column(name="image_id")
	private long imageId;
	private TweetType type;
	private String content;
	
	@OneToMany(mappedBy="tweet")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size=10)
	private Set<Comment> comments = new HashSet<Comment>();
	
	@OneToMany(mappedBy="tweet")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size=10)
	private Set<Love> likes = new HashSet<Love>();
	
	private TweetStatus status;
//	@Column(name="time_updated")
//	private Date timeUpdated;
//	@Column(name="time_created")
//	private Date timeCreated;
	
	public Tweet() {
	}
	
	public Tweet(TweetDTO tweet) {
		if (tweet.getTweetId() != null) {
			tweetId = tweet.getTweetId();
		}
		imageId = tweet.getImageId();
		type = tweet.getType();
		content = tweet.getContent();
		status = tweet.getStatus();
		timeUpdated = tweet.getTimeUpdated();
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
	public TweetStatus getStatus() {
		return status;
	}
	public void setStatus(TweetStatus status) {
		this.status = status;
	}
	public Date getTime_created() {
		return getTimeCreated();
	}
	public void setTime_created(Date time_created) {
		this.setTimeCreated(time_created);
	}
	public Set<Comment> getComments() {
		return comments;
	}
	public void setComments(Set<Comment> comments) {
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

	public Set<Love> getLikes() {
		return likes;
	}

	public void setLikes(Set<Love> likes) {
		this.likes = likes;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}
}
