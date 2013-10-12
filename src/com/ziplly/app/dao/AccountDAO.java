package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;

public interface AccountDAO {
	Account findByEmail(String email) throws NotFoundException;
	Account findByEmailAndPassword(String email, String password) throws NotFoundException;
	Account findById(Long accountId) throws NotFoundException;
	void save(Account user);
	void update(Account user);
	List<Account> getAll(int start, int end);
}
