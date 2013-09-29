package com.ziplly.app.dao;

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

import com.ziplly.app.model.TweetType;

@Entity
public class TweetDTO {
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
	private List<CommentDTO> comments;
	private int status;
	private Date time_created;
}
