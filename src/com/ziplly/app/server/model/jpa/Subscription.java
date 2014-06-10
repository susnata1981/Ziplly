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
import com.ziplly.app.server.util.TimeUtil;

@Entity
@Table(name = "subscription")
public class Subscription {
  private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="subscription_id")
	private Long subscriptionId;
	
  @OneToOne
  @JoinColumn(name="subscription_plan_id")
  private SubscriptionPlan subscriptionPlan;
  
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST} )
  @JoinColumn(name = "account_id")
  private Account account;
  
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="transaction_id")
	private Transaction transaction;
	
	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "time_updated")
	private long timeUpdated;
	
	@Column(name = "time_created")
	private long timeCreated;
	
	public Subscription() {
  }
	
	public Subscription(SubscriptionDTO subscription) {
		this.subscriptionId = subscription.getSubscriptionId();
		this.subscriptionPlan = new SubscriptionPlan(subscription.getSubscriptionPlan());
		this.transaction = new Transaction(subscription.getTransaction());
		this.status = subscription.getStatus().name();
		this.setTimeUpdated(TimeUtil.toTimestamp(subscription.getTimeUpdated()));
		this.setTimeCreated(TimeUtil.toTimestamp(subscription.getTimeCreated()));
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

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(long timeCreated) {
    this.timeCreated = timeCreated;
  }

  public long getTimeUpdated() {
    return timeUpdated;
  }

  public void setTimeUpdated(long timeUpdated) {
    this.timeUpdated = timeUpdated;
  }
}
