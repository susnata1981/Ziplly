package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long accountId;
	private String facebookId;
	private String accessToken;
	private String email;
	private String password;
	private String url;
	private String imageUrl;
	private int zip;
	private Date lastLoginTime;
	private Date timeCreated;
	private List<Tweet> tweets = new ArrayList<Tweet>();
	private Long uid;
	
	public AccountDTO() {
	}
	
	public AccountDTO(Account account) {
		this.setAccountId(account.getAccountId());
		this.password = account.getPassword();
		setFacebookId(account.getFacebookId());
		email = account.getEmail();
		url = account.getUrl();
		accessToken = account.getAccessToken();
		imageUrl = account.getImageUrl();
		setZip(account.getZip());
		setLastLoginTime(account.getLastLoginTime());
		setTimeCreated(account.getTimeCreated());
		this.setUid(account.getUid());
	}
	
	public Long getAccountId() {
		return accountId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "(" + email + ")";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Account)) {
			return false;
		}
		
		Account a = (Account)o;
		return a.getAccountId() == this.getAccountId();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	// to be overridden
	public String getDisplayName() {
		return "<NAME>";
	}

}
