package com.ziplly.app.model.overlay;

import java.io.Serializable;
import java.util.Date;

import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.TransactionDTO;

public class SubscriptionDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long subscriptionId;
  private SubscriptionPlanDTO subscriptionPlan;
	private TransactionDTO transaction;
	private String status;
	private Date timeUpdated;
	private Date timeCreated;
	
  public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public SubscriptionPlanDTO getSubscriptionPlan() {
		return subscriptionPlan;
	}
	public void setSubscriptionPlan(SubscriptionPlanDTO subscriptionPlan) {
		this.subscriptionPlan = subscriptionPlan;
	}
	public TransactionDTO getTransaction() {
		return transaction;
	}
	public void setTransaction(TransactionDTO transaction) {
		this.transaction = transaction;
	}
	public SubscriptionStatus getStatus() {
		return SubscriptionStatus.valueOf(status);
	}
	public void setStatus(SubscriptionStatus status) {
		this.status = status.name();
	}
	public Date getTimeUpdated() {
	  return timeUpdated;
  }
	public void setTimeUpdated(Date timeUpdated) {
	  this.timeUpdated = timeUpdated;
  }
	public Date getTimeCreated() {
	  return timeCreated;
  }
	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
  }
}
