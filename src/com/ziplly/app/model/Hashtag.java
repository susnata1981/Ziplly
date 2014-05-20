package com.ziplly.app.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "hashtag")
public class Hashtag extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "tag")
	private String tag;

	@ManyToMany(mappedBy = "hashtags")
	private Set<Tweet> tweets = new HashSet<Tweet>();

	public Hashtag() {
	}

	public Hashtag(HashtagDTO h) {
		this.id = h.getId();
		this.tag = h.getTag();
		// for (TweetDTO t : h.getTweets()) {
		// tweets.add(new Tweet(t));
		// }
	}

	public Set<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(Set<Tweet> tweets) {
		this.tweets = tweets;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addTweet(Tweet tweet) {
		tweets.add(tweet);
	}

	@Override
	public int hashCode() {
		if (tag != null) {
			return tag.hashCode();
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Hashtag)) {
			return false;
		}

		Hashtag result = (Hashtag) o;
		if (result.getId() != null) {
			return result.getId() == id;
		}

		return true;
	}
}
