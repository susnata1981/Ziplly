package com.ziplly.app.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
public class BusinessAccount extends Account {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;
	private String street1;
	private String street2;
	private BusinessType businessType;
	
	@OneToOne(fetch=FetchType.EAGER, mappedBy="seller")
	private Transaction transaction;
	
	public BusinessAccount() {
	}
	
	public BusinessAccount(BusinessAccountDTO account) {
		super(account);
		this.name = account.getName();
		this.phone = account.getPhone();
		this.website = account.getWebsite();
		this.street1 = account.getStreet1();
		this.street2 = account.getStreet2();
		this.businessType = account.getBusinessType();
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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType type) {
		this.businessType = type;
	}
}
