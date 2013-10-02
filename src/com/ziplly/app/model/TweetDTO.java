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
import javax.persistence.OneToMany;

@Entity
public class TweetDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long tweet_id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sender_id")
	private AccountDTO sender;
	@Column(name="image_id")
	private long imageId;
	private TweetType type;
	private String content;
	@ManyToMany(mappedBy="tweets")
	private Set<TagDTO> tags;
	@OneToMany(mappedBy="tweet")
	private List<CommentDTO> comments = new ArrayList<CommentDTO>();
	private int status;
	private Date time_created;
	
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
		return time_created;
	}
	public void setTime_created(Date time_created) {
		this.time_created = time_created;
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
	public long getTweet_id() {
		return tweet_id;
	}
	public void setTweet_id(long tweet_id) {
		this.tweet_id = tweet_id;
	}
}
