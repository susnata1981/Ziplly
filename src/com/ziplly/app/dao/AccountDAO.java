package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.QueryMetaData;

public interface AccountDAO {
	AccountDTO findByEmail(String email) throws NotFoundException;
	AccountDTO findById(Long accountId) throws NotFoundException;
	void save(AccountDTO user);
	List<AccountDTO> get(QueryMetaData qmd) throws IllegalArgumentException;
//	Set<AccountDTO> getAccounts(GetAccountDetailsRequest req);
	boolean save(AccountDetails ad);
	List<AccountDTO> getAll(int start, int end);
}
