package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class ImageDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String blobKey;
	private String url;
	private String status;
	private Date timeCreated;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	public RecordStatus getStatus() {
		return RecordStatus.valueOf(status);
	}

	public void setStatus(RecordStatus status) {
		this.status = status.name();
	}
}