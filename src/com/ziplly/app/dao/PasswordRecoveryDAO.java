package com.ziplly.app.dao;

import com.ziplly.app.server.model.jpa.PasswordRecovery;

public interface PasswordRecoveryDAO {
	void save(PasswordRecovery pr);

	PasswordRecovery findByHash(String hash);

	PasswordRecovery findByEmail(String email);

	void update(PasswordRecovery pr);

	void createOrUpdate(String email, String hash);
}
