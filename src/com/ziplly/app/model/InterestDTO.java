package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class InterestDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long interestId;
	private String name;
	private Date timeCreated;

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return interestId.hashCode() + name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof InterestDTO)) {
			return false;
		}

		InterestDTO i = (InterestDTO) o;
		return i.getInterestId() == interestId;
	}
}