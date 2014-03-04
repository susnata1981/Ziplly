package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="image_metadata")
public class Image extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="blob_key", nullable = false, updatable = false, length=512)
	private String blobKey;
	@Column(length=512, nullable = false, updatable = false)
	private String url;
	@Column(length=20)
	private String status;
	
	public Image() {
	}
	
	public Image(ImageDTO image) {
		this.id = image.getId();
		this.blobKey = image.getBlobKey();
		this.url = image.getUrl();
		this.setTimeCreated(image.getTimeCreated());
	}
	
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

	public RecordStatus getStatus() {
		return RecordStatus.valueOf(status);
	}

	public void setStatus(RecordStatus status) {
		this.status = status.name();
	}
}
