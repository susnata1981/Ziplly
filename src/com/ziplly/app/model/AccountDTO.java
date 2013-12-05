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
	private String neighborhood;
	private String city;
	private String state;
	private Role role;
	private AccountStatus status;
	private Date lastLoginTime;
	private Date timeUpdated;
	private Date timeCreated;
	private List<TweetDTO> tweets = new ArrayList<TweetDTO>();
	private Long uid;
	private List<AccountNotificationSettingsDTO> notificationSettings = new ArrayList<AccountNotificationSettingsDTO>();
	private List<PrivacySettingsDTO> privacySettings = new ArrayList<PrivacySettingsDTO>();
	
	public AccountDTO() {
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

	public List<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	// to be overridden
	public String getDisplayName() {
		return "<NAME>";
	}

	public List<AccountNotificationSettingsDTO> getNotificationSettings() {
		return notificationSettings;
	}

	public void setNotificationSettings(List<AccountNotificationSettingsDTO> notifications) {
		this.notificationSettings = notifications;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
		this.timeUpdated = timeUpdated;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<PrivacySettingsDTO> getPrivacySettings() {
		return privacySettings;
	}

	public void setPrivacySettings(List<PrivacySettingsDTO> privacySettings) {
		this.privacySettings = privacySettings;
	}
	
	public void addPrivacySettings(PrivacySettingsDTO ps) {
		privacySettings.add(ps);
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}
}
