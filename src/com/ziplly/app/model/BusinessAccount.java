package com.ziplly.app.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
// TODO (uncomment)
//@DiscriminatorValue("business")
public class BusinessAccount extends Account {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;
	private String street1;
	private String street2;
	private BusinessType businessType;
	private BusinessCategory category;
	
	@OneToOne(cascade=CascadeType.ALL)
	private BusinessProperties properties;
	
	@OneToMany(mappedBy="seller")
	@Fetch(FetchMode.JOIN)
	private Set<Transaction> transactions = new HashSet<Transaction>();
	
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
		this.category = account.getCategory();
		this.properties = new BusinessProperties(account.getProperties());
		
		for(TransactionDTO txn : account.getTransactions()) {
			this.transactions.add(new Transaction(txn));
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

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType type) {
		this.businessType = type;
	}

	public BusinessProperties getProperties() {
		return properties;
	}

	public void setProperties(BusinessProperties properties) {
		this.properties = properties;
	}

	public BusinessCategory getCategory() {
		return category;
	}

	public void setCategory(BusinessCategory category) {
		this.category = category;
	}
}
