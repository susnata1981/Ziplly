package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.model.overlay.SubscriptionDTO;
import com.ziplly.app.shared.StringUtil;

public class BusinessAccountDTO extends AccountDTO {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;
	private List<SubscriptionDTO> subscriptions = new ArrayList<SubscriptionDTO>();
	private String businessType;
	private String category;
	private BusinessPropertiesDTO properties;

	public BusinessAccountDTO() {
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

	@Override
	public String getDisplayName() {
		return StringUtil.capitalize(name);
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<SubscriptionDTO> getTransactions() {
		return getSubscriptions();
	}

	public void setTransactions(List<SubscriptionDTO> transactions) {
		this.setSubscriptions(transactions);
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

	public void setProperties(BusinessPropertiesDTO properties) {
		this.properties = properties;
	}

	public BusinessPropertiesDTO getProperties() {
		return properties;
	}

	public List<SubscriptionDTO> getSubscriptions() {
	  return subscriptions;
  }

	public void setSubscriptions(List<SubscriptionDTO> subscriptions) {
	  this.subscriptions = subscriptions;
  }
}
