package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="comment")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="comment_id")
	private int commentId;
	
	@ManyToOne
	@JoinColumn(name="tweet_id")
	private Tweet tweet;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account author;
	
	@OneToMany(mappedBy="comment")
	private Set<Love> likes = new HashSet<Love>();
	
	private String content;
	private Date timeCreated;
	
	public Comment() {
	}
	
	public Comment(CommentDTO comment) {
		this.setTweet(new Tweet(comment.getTweet()));
		this.author = new Account(comment.getAuthor());
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
	public Account getAuthor() {
		return author;
	}
	public void setAuthor(Account author) {
		this.author = author;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public Tweet getTweet() {
		return tweet;
	}
	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	public Set<Love> getLikes() {
		return likes;
	}

	public void setLikes(Set<Love> likes) {
		this.likes = likes;
	}
}
