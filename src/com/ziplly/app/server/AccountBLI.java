package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountAlreadySubscribedException;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TransactionDTO;

public interface AccountBLI {
//	AccountDTO register(Account account) throws AccountExistsException;
	AccountDTO register(Account account, boolean saveImage) throws AccountExistsException, InternalError, UnsupportedEncodingException, NoSuchAlgorithmException;
	AccountDTO validateLogin(String email,String password) throws InvalidCredentialsException, NotFoundException;
	AccountDTO updateAccount(Account account) throws NeedsLoginException, NotFoundException;
	void logout(Long uid) throws NotFoundException;
	AccountDTO getFacebookDetails(String code) throws OAuthException;
	AccountDTO getLoggedInUser() throws NotFoundException;
	String getImageUploadUrl();
	AccountDTO getAccountById(Long accountId) throws NotFoundException;
//	List<PersonalAccountDTO> getAccountByZip(AccountDTO account);
	Long doLogin(AccountDTO account);
//	Long doLogin(Account account);
	TransactionDTO pay(TransactionDTO transaction) throws AccountAlreadySubscribedException, DuplicateException, NotFoundException;
	void updatePassword(Account account, String oldPassword, String newPassword) throws InvalidCredentialsException, NotFoundException;
	void sendPasswordRecoveryEmail(String email) throws NotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException, DuplicateException;
	AccountDTO verifyPasswordRecoverLink(String hash) throws AccessError,NotFoundException;
	void resetPassword(Long accountId, String password) throws NotFoundException;
//	void sendEmail(Account sender, Account receiver, EmailTemplate template);
	Environment getEnvironment();
//	void setCurrentLocation(AccountDTO account, Long neighborhoodId);
	void resendEmailVerification(String email) throws NotFoundException, AccountExistsException, InternalError, UnsupportedEncodingException, NoSuchAlgorithmException;
}
