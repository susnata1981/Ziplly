package com.ziplly.app.server.model.jpa;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.util.TimeUtil;

@NamedQueries({
    @NamedQuery(
        name = "findTweetsByNeighborhood",
        query = "select t from Tweet t join t.targetNeighborhoods tn "
            + "where tn.neighborhoodId = :neighborhoodId and t.status = :status order by t.timeCreated desc"),
    @NamedQuery(
        name = "findTweetsById",
        query = "select t from Tweet t where t.tweetId = :tweetId order by t.timeCreated desc"),
    @NamedQuery(
        name = "findTweetsByAccountId",
        query = "select t from Tweet t where t.sender.accountId = :accountId and t.status = :status order by t.timeCreated desc"),
    @NamedQuery(
        name = "findTweetsByTypeAndNeighborhood",
        query = "select t from Tweet t join t.targetNeighborhoods tn where tn.neighborhoodId = :neighborhoodId and t.status = :status and t.type = :type order by t.timeCreated desc"), 
})
@Entity
@Table(name = "tweet")
public class Tweet extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tweet_id")
	private long tweetId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sender_id")
	@Fetch(FetchMode.JOIN)
	private Account sender;

	// What is this?
	@Column(name = "image_id")
	private long imageId;

	@OneToMany(cascade = CascadeType.PERSIST)
	@Fetch(FetchMode.JOIN)
	@JoinTable(name = "tweet_images", joinColumns = { @JoinColumn(name = "tweet_id") },
	    inverseJoinColumns = { @JoinColumn(name = "image_id") })
	private Set<Image> images = new HashSet<Image>();

	@Column(length = 512)
	private String content;

	@Column(nullable = false)
	private String type;

	@OneToMany(mappedBy = "tweet")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size = 10)
	private Set<Comment> comments = new HashSet<Comment>();

	@OneToMany(mappedBy = "tweet")
	@Fetch(FetchMode.JOIN)
	@BatchSize(size = 10)
	private Set<Love> likes = new HashSet<Love>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "tweet_hashtag", joinColumns = { @JoinColumn(name = "tweet_id") },
	    inverseJoinColumns = { @JoinColumn(name = "id") })
	private Set<Hashtag> hashtags = new HashSet<Hashtag>();

	@Column(name = "status")
	private String status;

	// Currently supporting only 1 image.
	@Column(name = "image_url")
	private String image;

	@ManyToMany
	@JoinTable(name = "tweet_neighborhood", joinColumns = { @JoinColumn(name = "tweet_id") },
	    inverseJoinColumns = { @JoinColumn(name = "neighborhood_id") })
	private Set<Neighborhood> targetNeighborhoods = new HashSet<Neighborhood>();

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="coupon_id")
	private Coupon coupon;
	
	@Column(name = "creation_time")
	private long creationTime;

	public Tweet() {
	}

	public Tweet(TweetDTO tweet) {
		if (tweet.getTweetId() != null) {
			tweetId = tweet.getTweetId();
		}
		
		imageId = tweet.getImageId();
		type = tweet.getType().name();
		content = tweet.getContent();
		status = tweet.getStatus().name();
		setTimeUpdated(tweet.getTimeUpdated());
		setTimeCreated(tweet.getTimeCreated());
		this.creationTime = TimeUtil.toTimestamp(tweet.getTimeCreated());
		setImage(tweet.getImage());
		
		for (NeighborhoodDTO n : tweet.getTargetNeighborhoods()) {
			targetNeighborhoods.add(new Neighborhood(n));
		}

		for (ImageDTO image : tweet.getImages()) {
			images.add(new Image(image));
		}
		
		if (tweet.getCoupon() != null) {
			this.coupon = new Coupon(tweet.getCoupon());
		}
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

	public Set<Love> getLikes() {
		return likes;
	}

	public void setLikes(Set<Love> likes) {
		this.likes = likes;
	}

	public void addHashtag(Hashtag h) {
		hashtags.add(h);
	}

	public void setHashtags(Set<Hashtag> h) {
		hashtags.clear();
		hashtags = h;
	}

	public Set<Hashtag> getHashtags() {
		return hashtags;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<Neighborhood> getTargetNeighborhoods() {
		return targetNeighborhoods;
	}

	public void setTargetNeighborhoods(Set<Neighborhood> targetNeighborhoods) {
		this.targetNeighborhoods = targetNeighborhoods;
	}

	@Override
	public String toString() {
		return "Content:  " + content + " Type: " + type + " Sender: " + sender.getEmail();
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Coupon getCoupon() {
	  return coupon;
  }

	public void setCoupon(Coupon coupon) {
	  this.coupon = coupon;
  }

  public long getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }
}
