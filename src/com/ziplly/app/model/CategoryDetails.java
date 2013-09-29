package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	public Category category;
	public List<Account> accounts = new ArrayList<Account>();
	
	public void addAccount(Account a) {
		accounts.add(a);
	}
}
