package com.ziplly.app.server.bli;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.AccountNotActiveException;
import com.ziplly.app.client.exceptions.CouponCampaignEndedException;
import com.ziplly.app.client.exceptions.CouponPurchaseLimitExceededException;
import com.ziplly.app.client.exceptions.CouponSoldOutException;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Coupon;

public interface AccountBLI {
	// AccountDTO register(Account account) throws AccountExistsException;
	Account register(Account account, boolean saveImage) throws AccountExistsException,
	    InternalException,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException;

	Account validateLogin(String email, String password) throws InvalidCredentialsException,
	    NotFoundException, AccountNotActiveException;

	Account updateAccount(Account account) throws NeedsLoginException, NotFoundException;

	void logout(Long uid) throws NotFoundException;

	Account getFacebookDetails(String code) throws OAuthException;

	AccountDTO getLoggedInUser() throws NotFoundException;

	String getImageUploadUrl();

	Account getAccountById(Long accountId) throws NotFoundException;

	// List<PersonalAccountDTO> getAccountByZip(AccountDTO account);
	Long doLogin(Account account);

	void
	    updatePassword(Account account, String oldPassword, String newPassword) throws InvalidCredentialsException,
	        NotFoundException;

	void sendPasswordRecoveryEmail(String email) throws NotFoundException,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException,
	    DuplicateException;

	Account verifyPasswordRecoverLink(String hash) throws AccessException, NotFoundException;

	void resetPassword(Long accountId, String password) throws NotFoundException,
	    InvalidCredentialsException;

	// void sendEmail(Account sender, Account receiver, EmailTemplate template);
	Environment getEnvironment();

	// void setCurrentLocation(AccountDTO account, Long neighborhoodId);
	void resendEmailVerification(String email) throws NotFoundException,
	    AccountExistsException,
	    InternalException,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException;
	
//	void checkAccountEligibleForCouponPurchase(Account account, Coupon coupon) throws DispatchException;
	
  void checkAccountEligibleForCouponPurchase(Account account, Coupon coupon) 
      throws CouponSoldOutException, CouponCampaignEndedException, CouponPurchaseLimitExceededException;
}
