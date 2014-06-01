package com.ziplly.app.server.model.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.model.SubscriptionStatus;
import com.ziplly.app.model.overlay.SubscriptionDTO;

@Entity
@Table(name = "subscription")
public class Subscription extends AbstractEntity {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="subscription_id")
	private Long subscriptionId;
	
  @OneToOne
  @JoinColumn(name="subscription_plan_id")
  private SubscriptionPlan subscriptionPlan;
  
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="transaction_id")
	private Transaction transaction;
	
	@Column(name = "status", nullable = false)
	private String status;

	public Subscription() {
  }
	
	public Subscription(SubscriptionDTO subscription) {
		this.subscriptionId = subscription.getSubscriptionId();
		this.subscriptionPlan = new SubscriptionPlan(subscription.getSubscriptionPlan());
		this.transaction = new Transaction(subscription.getTransaction());
		this.status = subscription.getStatus().name();
		this.timeUpdated = subscription.getTimeUpdated();
		this.timeCreated = subscription.getTimeCreated();
  }
	
	public Long getSubscriptionId() {
	  return subscriptionId;
  }

	public void setSubscriptionId(Long subscriptionId) {
	  this.subscriptionId = subscriptionId;
  }

	public Transaction getTransaction() {
	  return transaction;
  }

	public void setTransaction(Transaction transaction) {
	  this.transaction = transaction;
  }

	public SubscriptionStatus getStatus() {
	  return SubscriptionStatus.valueOf(status);
  }

	public void setStatus(SubscriptionStatus status) {
	  this.status = status.name();
  }

	public SubscriptionPlan getSubscriptionPlan() {
	  return subscriptionPlan;
  }

	public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
	  this.subscriptionPlan = subscriptionPlan;
  }
}
