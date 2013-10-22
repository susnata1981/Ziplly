package com.ziplly.app.model;

import javax.persistence.Entity;

@Entity
public class BusinessAccount extends Account {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String phone;
	private String street1;
	private String street2;
	
	public BusinessAccount() {
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
	
}
