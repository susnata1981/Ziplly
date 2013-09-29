package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
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
	private Date lastLoginTime;
	private Date timeCreated;
	
	public Account() {
	}
	
//	public Account(Account a) {
//		this.id = a.id;
//		this.fId = a.fId;
//		this.email = a.email;
//		this.firstName = a.firstName;
//		this.lastName = a.lastName;
//		this.url = a.url;
//		this.accessToken = a.accessToken;
//		this.imageUrl = a.imageUrl;
//		this.introduction = a.introduction;
//		this.city = a.city;
//		this.state = a.state;
//		this.longitude = a.longitude;
//		this.latitude = a.latitude;
//		this.lastLoginTime = a.lastLoginTime;
//		this.timeCreated = a.timeCreated;
//	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return a.id == this.id;
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
	
	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}
	
	public String getImageUrl(int width,int height) {
		String imgUrl = getImageUrl();
		int index = imgUrl.indexOf("?");
		if (index != 0) {
			String newImgUrl = imgUrl.substring(0, index);
			return newImgUrl + "?widht="+width+"&height="+height;
		}
		return getImageUrl();
	}

}
