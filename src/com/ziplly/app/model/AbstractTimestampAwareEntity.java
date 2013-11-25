package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class AbstractTimestampAwareEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="time_created")
	protected Date timeCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="time_updated")
	protected Date timeUpdated;
	
	@PrePersist
	protected void onCreate() {
		timeCreated = timeUpdated = new Date();
	}
	
	@PreUpdate
	protected void onUpdate() {
		timeUpdated = new Date();
	}
}
