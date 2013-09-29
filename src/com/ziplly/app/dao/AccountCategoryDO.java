package com.ziplly.app.dao;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;
import com.literati.app.shared.Account;
import com.literati.app.shared.Category;

@SuppressWarnings("serial")
@Entity
public class AccountCategoryDO implements Serializable {
	@Id
	private Long id;

	@Load
	@Index
	private Ref<Category> category;
	
	@Load
	@Index
	@Parent
	private Ref<Account> account;
	
	private Category fakeCategory;

	private Account fakeAccount;
	
	public Category getCategory() {
		return category.get();
	}
	public void setCategory(Key<Category> category){
		this.category = Ref.create(category);
	}
	public Account getAccount() {
		return account.get();
	}
	public void setAccount(Key<Account> account) {
		this.account = Ref.create(account);
	}
	public Ref<Account> getAccountRef() {
		return account;
	}
	public Ref<Category> getCategoryRef() {
		return category;
	}
	public Category getFakeCategory() {
		return fakeCategory;
	}
	public void setFakeCategory(Category fakeCategory) {
		this.fakeCategory = fakeCategory;
	}
	public void setFakeAccount(Account a) {
		this.fakeAccount = a;
	}
	public Account getFakeAccount() {
		return fakeAccount;
	}
}
