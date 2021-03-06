package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public interface AccountDAO {
	AccountDTO findByEmail(String email) throws NotFoundException;
	AccountDTO findByEmailAndPassword(String email, String password) throws NotFoundException;
	AccountDTO findById(Long accountId) throws NotFoundException;
	AccountDTO save(Account user);
	AccountDTO update(Account user);
	List<Account> getAll(int start, int end);
	List<PersonalAccountDTO> findByZip(int zip);
	void updatePassword(Account acct);
	List<AccountDTO> findAll();
	List<AccountDTO> findAccounts(String query, int start, int end);
	Long findTotalAccounts(String countQuery);
}
