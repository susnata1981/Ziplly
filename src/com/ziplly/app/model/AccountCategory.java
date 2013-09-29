package com.ziplly.app.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountCategory implements Serializable {
	private Long id;
	private Category category;
	
	private Account account;
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category){
		this.category = category;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
