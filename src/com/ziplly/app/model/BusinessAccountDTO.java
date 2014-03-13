package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.shared.StringUtil;

public class BusinessAccountDTO extends AccountDTO {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;
	private String street1;
	private String street2;
	private List<TransactionDTO> transactions = new ArrayList<TransactionDTO>();
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

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
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

	public List<TransactionDTO> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionDTO> transactions) {
		this.transactions = transactions;
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
}
