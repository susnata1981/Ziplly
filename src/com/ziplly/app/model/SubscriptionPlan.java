package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "subscription_id")
	private Long subscriptionId;
	private String name;
	private String description;
	@Column(name = "tweets_allowed")
	private int tweetsAllowed;
	@Column(name = "status")
	private String status;
	private Double fee;

	public SubscriptionPlan() {
	}

	public SubscriptionPlan(SubscriptionPlanDTO plan) {
		if (plan != null) {
			this.subscriptionId = plan.getSubscriptionId();
			this.name = plan.getName();
			this.description = plan.getDescription();
			this.fee = plan.getFee();
			setTimeCreated(plan.getTimeCreated());
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

	public int getTweetsAllowed() {
		return tweetsAllowed;
	}

	public void setTweetsAllowed(int tweetsAllowed) {
		this.tweetsAllowed = tweetsAllowed;
	}

	public SubscriptionPlanStatus getStatus() {
		return SubscriptionPlanStatus.valueOf(status);
	}

	public void setStatus(SubscriptionPlanStatus subscriptionPlanStatus) {
		this.status = subscriptionPlanStatus.name();
	}
}
