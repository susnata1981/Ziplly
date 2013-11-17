package com.ziplly.app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long subscriptionId;
	private String name;
	private String description;
	private Double fee;
	private Date timeCreated;

	public SubscriptionPlan() {
	}

	public SubscriptionPlan(SubscriptionPlanDTO plan) {
		if (plan != null) {
			this.subscriptionId = plan.getSubscriptionId();
			this.name = plan.getName();
			this.description = plan.getDescription();
			this.fee = plan.getFee();
			this.timeCreated = plan.getTimeCreated();
		}
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
}
