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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(
		name = "findLikeByCommentAndUserId",
		query = "from Love as l where l.comment.commentId = :commentId and l.author.accountId = :accountId"
	),
	@NamedQuery(
		name = "findLikeByTweetAndUserId",
		query = "from Love as l where l.tweet.tweetId = :tweetId and l.author.accountId = :accountId"
	)
})
@Entity
@Table(name="love")
public class Love implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long likeId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="tweet_id")
	Tweet tweet;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="comment_id")
	Comment comment;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="account_id")
	Account author;
	
	Date timeCreated;

	public Love() {
	}
	
	public Love(LoveDTO like) {
		this.likeId = like.getLikeId();
		this.comment = new Comment(like.getComment());
		this.tweet = new Tweet(like.getTweet());
		this.timeCreated = like.getTimeCreated();
	}
	
	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Account getAuthor() {
		return author;
	}

	public void setAuthor(Account author) {
		this.author = author;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getLikeId() {
		return likeId;
	}

	public void setLikeId(Long likeId) {
		this.likeId = likeId;
	}
}
