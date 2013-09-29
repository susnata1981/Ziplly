package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.QueryMetaData;

public interface AccountDAO {
	Account findByEmail(String email) throws NotFoundException;
	Account findById(Long accountId) throws NotFoundException;
	void save(Account user);
	List<Account> get(QueryMetaData qmd) throws IllegalArgumentException;
//	Set<Account> getAccounts(GetAccountDetailsRequest req);
	boolean save(AccountDetails ad);
	List<Account> getAll(int start, int end);
}
