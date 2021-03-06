package com.ziplly.app.dao;

import com.ziplly.app.model.AccountRegistration;

public interface AccountRegistrationDAO {
	void create(AccountRegistration ar);
	AccountRegistration findById(Long id);
	AccountRegistration findByEmail(String email);
	AccountRegistration findByEmailAndCode(String email, long code);
	void update(AccountRegistration ar);
}
