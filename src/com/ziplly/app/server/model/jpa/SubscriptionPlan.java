package com.ziplly.app.server.model.jpa;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.SubscriptionPlanStatus;
import com.ziplly.app.model.SubscriptionPlanType;

@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "subscription_id")
	private Long subscriptionId;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@Column(name = "tweets_allowed")
	private int tweetsAllowed;
	@Column(name = "coupons_allowed")
	private int couponsAllowed;
	@Column(name="plan_type", nullable = false)
	private String planType;
	@Column(name = "status", nullable = false)
	private String status;
	@Column(nullable = false)
	private BigDecimal fee;

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

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
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

	public int getCouponsAllowed() {
	  return couponsAllowed;
  }

	public void setCouponsAllowed(int couponsAllowed) {
	  this.couponsAllowed = couponsAllowed;
  }

	public SubscriptionPlanType getPlanType() {
	  return SubscriptionPlanType.valueOf(planType);
  }

	public void setPlanType(SubscriptionPlanType planType) {
	  this.planType = planType.name();
  }
}
