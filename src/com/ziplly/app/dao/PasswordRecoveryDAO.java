package com.ziplly.app.dao;

import com.ziplly.app.model.PasswordRecovery;

public interface PasswordRecoveryDAO {
	void save(PasswordRecovery pr);
	PasswordRecovery findByHash(String hash);
	PasswordRecovery findByEmail(String email);
	void update(PasswordRecovery pr);
}
