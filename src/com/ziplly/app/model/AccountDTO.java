package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long account_id;
	private String fId;
	private String email;
	private String firstName;
	private String lastName;
	private String url;
	private String accessToken;
	private String imageUrl;
	private String introduction;
	private String city;
	private String state;
	private int zip;
	private String longitude;
	private String latitude;
	private List<AccountSettingsDTO> accountSettings = new ArrayList<AccountSettingsDTO>();
	private Date lastLoginTime;
	private Date timeCreated;
	
	private List<TweetDTO> tweets = new ArrayList<TweetDTO>();
	
	public AccountDTO() {
	}
	
	public AccountDTO(Account account) {
		account_id = account.getId();
		fId = account.getfId();
		firstName = account.getFirstName();
		lastName = account.getLastName();
		email = account.getEmail();
		url = account.getUrl();
		accessToken = account.getAccessToken();
		imageUrl = account.getImageUrl();
		introduction = account.getIntroduction();
		city = account.getCity();
		state = account.getState();
		zip = account.getZip();
		longitude = account.getLongitude();
		latitude = account.getLatitude();
		for(AccountSettings as : account.getAccountSettings()) {
			AccountSettingsDTO asd = new AccountSettingsDTO(as);
			accountSettings.add(asd);
		}
		lastLoginTime = account.getLastLoginTime();
		timeCreated = account.getTimeCreated();
	}
	
	public Long getId() {
		return account_id;
	}

	public void setId(Long id) {
		this.account_id = id;
	}

	public String getDisplayName() {
		return getFirstName() +" "+ getLastName();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getDisplayName() + " : (" + email + ")";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
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
		return a.getId() == this.account_id;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date date) {
		this.lastLoginTime = date;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public void setLocation(String location) {
		String [] locationMetadata = location.split(",");
		this.city = locationMetadata[0].trim().toLowerCase();
		this.state = locationMetadata[1].trim().toLowerCase();
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

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude.toString();
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude.toString();
	}
	
	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public List<TweetDTO> getTweets() {
		return tweets;
	}

	public void setTweets(List<TweetDTO> tweets) {
		this.tweets = tweets;
	}

	public List<AccountSettingsDTO> getAccountSettings() {
		return accountSettings;
	}

	public void setAccountSettings(List<AccountSettingsDTO> accountSettings) {
		this.accountSettings = accountSettings;
	}

}
