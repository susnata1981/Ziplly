package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TweetDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long tweetId;
	private AccountDTO sender;
	private long imageId;
	private String type;
	private String content;
	private List<CommentDTO> comments = new ArrayList<CommentDTO>();
	private List<LoveDTO> likes = new ArrayList<LoveDTO>();
	private String status;
	private String image;
	private Set<NeighborhoodDTO> targetNeighborhoods = new HashSet<NeighborhoodDTO>();
	private Date timeUpdated;
	private Date timeCreated;
	
	public TweetDTO() {
	}
	
	public TweetType getType() {
		return TweetType.valueOf(type);
	}
	public void setType(TweetType type) {
		this.type = type.name();
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TweetStatus getStatus() {
		return TweetStatus.valueOf(status);
	}
	public void setStatus(TweetStatus status) {
		this.status = status.name();
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

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<NeighborhoodDTO> getTargetNeighborhoods() {
		return targetNeighborhoods;
	}

	public void setTargetNeighborhoods(Set<NeighborhoodDTO> targetNeighborhoods) {
		this.targetNeighborhoods = targetNeighborhoods;
	}
	
	@Override
	public String toString() {
		return "tweetId: "+tweetId+" Content:  "+content+" Type: "+type +" Sender: "+sender.getEmail();
	}
}
