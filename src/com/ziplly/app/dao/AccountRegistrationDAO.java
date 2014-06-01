package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.server.model.jpa.AccountRegistration;

public interface AccountRegistrationDAO {
	void save(AccountRegistration ar);

	AccountRegistration findById(Long id);

	AccountRegistration findByEmail(String email);

	AccountRegistration findByEmailAndCode(String email, String code);

	void update(AccountRegistration ar);

	void findAndVerifyAccount(Long id, String code) throws NotFoundException, DuplicateException;

	AccountRegistration findByEmailAndCode(String email, long code);
}
