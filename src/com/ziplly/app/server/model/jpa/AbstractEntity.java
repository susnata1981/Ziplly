package com.ziplly.app.server.model.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time_created", updatable = false)
	private Date timeCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time_updated")
	private Date timeUpdated;

	@PrePersist
	protected void onCreate() {
		setTimeCreated(new Date());
	}

	@PreUpdate
	protected void onUpdate() {
		setTimeUpdated(new Date());
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
	}

	public Date getTimeUpdated() {
		return timeUpdated;
	}

	public void setTimeUpdated(Date timeUpdated) {
	  this.timeUpdated = timeUpdated;
	}
}
