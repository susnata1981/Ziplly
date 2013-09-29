package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountCategory;
import com.ziplly.app.model.Category;

public interface AccountCategoryDAO {
	boolean save(List<AccountCategory> acList);
	List<Account> getAccountByCategoryId(Long categoryId);
	List<Category> getCategories(Account account);
	boolean save(AccountCategory ac);
	List<Account> getAccountsByCategoryNames(List<String> categories);
}
