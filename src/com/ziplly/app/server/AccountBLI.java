package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountAlreadySubscribedException;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TransactionDTO;

public interface AccountBLI {
	AccountDTO register(Account account) throws AccountExistsException;
	AccountDTO validateLogin(String email,String password) throws InvalidCredentialsException, NotFoundException;
	AccountDTO updateAccount(Account account) throws NeedsLoginException;
	void logout(Long uid) throws NotFoundException;
	AccountDTO getFacebookDetails(String code) throws OAuthException;
	AccountDTO getLoggedInUser() throws NotFoundException;
	String getImageUploadUrl();
	AccountDTO getAccountById(Long accountId) throws NotFoundException;
	List<PersonalAccountDTO> getAccountByZip(AccountDTO account);
	Long doLogin(AccountDTO account);
	Long doLogin(Account account);
	TransactionDTO pay(TransactionDTO transaction) throws AccountAlreadySubscribedException;
	void updatePassword(Account account, String oldPassword, String newPassword) throws InvalidCredentialsException, NotFoundException;
	void sendPasswordRecoveryEmail(String email) throws NotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException, DuplicateException;
	AccountDTO verifyPasswordRecoverLink(String hash) throws AccessError,NotFoundException;
	void resetPassword(Long accountId, String password) throws NotFoundException;
}
