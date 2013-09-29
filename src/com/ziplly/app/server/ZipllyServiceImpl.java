package com.ziplly.app.server;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.restfb.types.User;
import com.ziplly.app.client.ZipllyService;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountDAOImpl;
import com.ziplly.app.dao.AccountDTO;
import com.ziplly.app.dao.NotFoundException;
import com.ziplly.app.facebook.dao.FUserDAOFactory;
import com.ziplly.app.facebook.dao.IFUserDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.Category;
import com.ziplly.app.model.LatLong;
import com.ziplly.app.server.oauth.AuthFlowManagerFactory;
import com.ziplly.app.server.oauth.OAuthFlowManager;

public class ZipllyServiceImpl extends RemoteServiceServlet implements ZipllyService{
	private static final long serialVersionUID = 1L;
	private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	
	private AccountDAO accountDAO = new AccountDAOImpl();
	
	@Override
	public AccountDetails getAccessToken(String code) throws Exception {
		OAuthFlowManager authFlowManager = AuthFlowManagerFactory
				.get(authConfig);
		AccessToken token = authFlowManager.exchange(code);

		IFUserDAO fUserDao = FUserDAOFactory.getFUserDao(token
				.getAccess_token());
		User fuser = fUserDao.getUser();
		LatLong locInfo = fUserDao.getLocationInfo(fuser);

		if (fuser == null) {
			return null;
		}

		AccountDTO account = null;
		boolean newAccount = false;

		try {
			account = accountDAO.findByEmail(fuser.getEmail());
		} catch (NotFoundException nfe) {
			// create user
			newAccount = true;
			account = new AccountDTO();
			account.setEmail(fuser.getEmail());
			account.setFirstName(fuser.getFirstName());
			account.setLastName(fuser.getLastName());
			account.setAccessToken(token.getAccess_token());
			account.setfId(fuser.getId());
			account.setLocation(fuser.getLocation().getName());
			account.setUrl(fuser.getLink());
			account.setLongitude(locInfo.longitude);
			account.setLatitude(locInfo.latitude);
			account.setTimeCreated(new Date());
			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId()
					+ "/picture?width=200&height=160";
			account.setImageUrl(imgUrl);
		}

		// update access token for existing user
		if (!newAccount) {
			account.setAccessToken(token.getAccess_token());
		}

		// save
		accountDAO.save(account);

//		if (!save) {
//			throw new RuntimeException("Couldn't save account");
//		}

		// Save account preferences
//		FacebookUserInterest fui = fUserDao.getInterests();
//		saveAccountPreferences(account, ServiceUtil.transform(fui));

		// TODO remove double lookup of categories
		// login
		return doLogin(account);
	}

	// TODO change the input param
	protected AccountDetails saveAccountPreferences(Account account,
			List<Category> input) {
		
		return null;
	}
	
	private AccountDetails doLogin(AccountDTO account) {
		// update account last login timestamp
//		account.setLastLoginTime(new Date());
//		accountDAO.save(account);

		AccountDetails ad = new AccountDetails();
		ad.account = ServiceUtil.copy(account);
//		List<Category> categoriesForAccount = getCategoriesForAccount(account);
//		if (categoriesForAccount != null) {
//			ad.categories.addAll(categoriesForAccount);
//		}

		// save account in session
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession();
		session.setAttribute(LOGGED_IN_USER, ad);
		return ad;
	}
	
	@Override
	public AccountDetails getLoggedInUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountDetails loginAccountById(long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account getAccountById(long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logoutAccount() {
		// TODO Auto-generated method stub
		
	}

}
