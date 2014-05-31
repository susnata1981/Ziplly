package com.ziplly.app.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ziplly.app.model.overlay.SubscriptionDTO;

@Entity
@DiscriminatorValue("business")
public class BusinessAccount extends Account {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;

	@Column(name = "business_type")
	private String businessType;

	private String category;

	@OneToOne(cascade = CascadeType.ALL)
	private BusinessProperties properties;

	@OneToMany
	@JoinColumn(name="subscription_id")
	@Fetch(FetchMode.JOIN)
	private Set<Subscription> subscriptions = new HashSet<Subscription>();

	public BusinessAccount() {
	}

	public BusinessAccount(BusinessAccountDTO account) {
		super(account);
		this.name = account.getName();
		this.phone = account.getPhone();
		this.website = account.getWebsite();
		this.businessType = BusinessType.COMMERCIAL.name();
		this.category = account.getCategory().name();
		this.properties = new BusinessProperties(account.getProperties());
		
		for(SubscriptionDTO subscription : account.getTransactions()) {
			this.subscriptions.add(new Subscription(subscription));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Set<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public BusinessProperties getProperties() {
		return properties;
	}

	public void setProperties(BusinessProperties properties) {
		this.properties = properties;
	}

	public BusinessType getBusinessType() {
		return BusinessType.valueOf(businessType);
	}

	public void setBusinessType(BusinessType type) {
		this.businessType = type.name();
	}

	public BusinessCategory getCategory() {
		return BusinessCategory.valueOf(category);
	}

	public void setCategory(BusinessCategory category) {
		this.category = category.name();
	}
}
