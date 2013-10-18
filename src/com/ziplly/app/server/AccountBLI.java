package com.ziplly.app.server;

import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;

public interface AccountBLI {
	Account register(Account account) throws AccountExistsException;
	Account validateLogin(String email,String password) throws InvalidCredentialsException, NotFoundException;
	Account updateAccount(Account account) throws NeedsLoginException;
	Long doLogin(Account account);
	void logout(Long uid) throws NotFoundException;
	AccountDTO getFacebookDetails(String code) throws OAuthException;
	Account getLoggedInUser() throws NotFoundException;
	String getImageUploadUrl();
	Account getAccountById(Long accountId) throws NotFoundException;
}
