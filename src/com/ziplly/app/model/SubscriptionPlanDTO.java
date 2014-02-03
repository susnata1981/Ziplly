package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

public class SubscriptionPlanDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long subscriptionId;
	private String name;
	private String description;
	private Double fee;
	private String status;
	private Date timeCreated;
	
	public SubscriptionPlanDTO() {
	}
	
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof SubscriptionPlanDTO)) {
			return false;
		}
		
		SubscriptionPlanDTO s = (SubscriptionPlanDTO)o;
		return s.getSubscriptionId() == subscriptionId;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + description.hashCode() + subscriptionId.hashCode();
	}

	public SubscriptionPlanStatus getStatus() {
		return SubscriptionPlanStatus.valueOf(status);
	}

	public void setStatus(SubscriptionPlanStatus status) {
		this.status = status.name();
	}
}
