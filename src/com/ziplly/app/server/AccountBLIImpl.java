package com.ziplly.app.server;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.restfb.types.User;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.facebook.dao.FUserDAOFactory;
import com.ziplly.app.facebook.dao.IFUserDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSetting;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.oauth.AuthFlowManagerFactory;
import com.ziplly.app.server.oauth.OAuthFlowManager;
import com.ziplly.app.shared.BCrypt;

public class AccountBLIImpl implements AccountBLI {
	private static final String UPLOAD_SERVICE_ENDPOINT = "/upload";
	protected final long hoursInMillis = 2 * 60 * 60 * 1000;
	private AccountDAO accountDao;
	private SessionDAO sessionDao;
	private BlobstoreService blobstoreService;

	@Inject
	protected Provider<HttpSession> httpSession;
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	Logger logger = Logger.getLogger(AccountBLIImpl.class.getCanonicalName());
	private InterestDAO interestDao;

	@Inject
	public AccountBLIImpl(AccountDAO accountDao, SessionDAO sessionDao,
			InterestDAO interestDao) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.interestDao = interestDao;
		this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		createInterestEntries();
	}

	private void createInterestEntries() {
		for (Activity a : Activity.values()) {
			Interest i = new Interest();
			i.setName(a.name().toLowerCase());
			i.setTimeCreated(new Date());
			interestDao.save(i);
		}
	}

	// TODO should we throw exception for invalid attempt?
	@Override
	public Account getLoggedInUser() throws NotFoundException {
		Long uid = getUidFromCookie();

		if (uid == null) {
			return null;
		}

		Session session = null;
		session = sessionDao.findSessionByUid(uid);
		System.out.println("Found session : " + session.getId());
		if (isValidSession(session)) {
			Account account = session.getAccount();
			account.setUid(uid);
			return account;
		}
		System.out.println("Invalid session....");
		return null;
	}

	@Override
	public Account getAccountById(Long accountId) throws NotFoundException {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		Account account = accountDao.findById(accountId);
		return account;
	}

	@Override
	public Account register(Account account) throws AccountExistsException {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		// check for existing account
		Account existingAccount = null;
		try {
			existingAccount = accountDao.findByEmail(account.getEmail());
		} catch (NotFoundException nfe) {
		}

		if (existingAccount != null) {
			throw new AccountExistsException(
					"Account already exists with this email: "
							+ existingAccount.getEmail());
		}

		if (account instanceof PersonalAccount) {
			createDefaultAccountSettings((PersonalAccount) account);
		}

		// create account otherwise
		accountDao.save(account);

		// login user
		Long uid = doLogin(account);

		account.setUid(uid);
		return account;
	}

	private void createDefaultAccountSettings(PersonalAccount account) {
		for (AccountDetailsType adt : AccountDetailsType.values()) {
			AccountSetting as = new AccountSetting();
			as.setSection(adt);
			as.setSetting(ShareSetting.PUBLIC);
			as.setAccount(account);
			account.getAccountSettings().add(as);
		}
	}

	@Override
	public Account validateLogin(String email, String password)
			throws InvalidCredentialsException, NotFoundException {
		Account account = null;
		try {
			account = accountDao.findByEmail(email);
			String storedPassword = account.getPassword();
			boolean checkpw = BCrypt.checkpw(password, storedPassword);
			if (!checkpw) {
				throw new InvalidCredentialsException();
			}
		} catch (NotFoundException e) {
			throw e;
		}

		// login user
		Long uid = doLogin(account);
		account.setUid(uid);
		return account;
	}

	@Override
	public Long doLogin(Account account) {
		Session session = new Session();
		session.setAccount(account);
		Date currTime = new Date();
		Date expireAt = new Date(currTime.getTime() + hoursInMillis);
		session.setExpireAt(expireAt);
		session.setTimeCreated(currTime);
		Long uid = UUID.randomUUID().getMostSignificantBits();
		session.setUid(uid);
		sessionDao.save(session);
		storeCookie(uid);
		return uid;
	}

	@Override
	public void logout(Long uid) throws NotFoundException {
		if (uid == null) {
			throw new IllegalArgumentException();
		}

		Long uidInCookie = getUidFromCookie();
		Long uidInRequest = uid;
		if (uidInCookie == null || !uidInCookie.equals(uidInRequest)) {
			throw new IllegalAccessError();
		}

		Session session = sessionDao.findSessionByUid(uidInRequest);
		if (session == null) {
			logger.log(Level.WARNING, String.format(
					"Session %l exists in cookie but not in session table",
					uidInCookie));
		}

		sessionDao.removeByUid(session.getUid());
		clearCookie();
	}

	@Override
	public AccountDTO getFacebookDetails(String code) throws OAuthException {

		Account loggedInAccount = getLoggedInUserBasedOnCookie();
		if (loggedInAccount != null) {
			return new AccountDTO(loggedInAccount);
		}

		OAuthFlowManager authFlowManager = AuthFlowManagerFactory
				.get(authConfig);
		AccessToken token;
		try {
			token = authFlowManager.exchange(code);
		} catch (Exception e) {
			throw new OAuthException();
		}
		IFUserDAO fUserDao = FUserDAOFactory.getFUserDao(token
				.getAccess_token());
		User fuser = fUserDao.getUser();

		if (fuser == null) {
			return null;
		}

		Account response = null;
		try {
			response = accountDao.findByEmail(fuser.getEmail());
		} catch (NotFoundException nfe) {
			System.out.println("didn't find account.");
			// create user
			PersonalAccountDTO account = new PersonalAccountDTO();
			account.setEmail(fuser.getEmail());
			account.setFirstName(fuser.getFirstName());
			account.setLastName(fuser.getLastName());
			account.setAccessToken(token.getAccess_token());
			account.setFacebookId(fuser.getId());
			account.setUrl(fuser.getLink());
			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId()
					+ "/picture"; // ?width=200&height=160
			account.setImageUrl(imgUrl);
			return account;
		} catch (Exception e) {
			System.out
					.println("Exception caught while getting facebook user details:"
							+ e);
		}

		// login user
		Long uid = doLogin(response);
		AccountDTO result = new AccountDTO(response);
		result.setUid(uid);
		System.out.println("Returning existing account:" + response);
		return result;
	}

	@Override
	public Account updateAccount(Account account) throws NeedsLoginException {
		if (!isValidSession()) {
			throw new NeedsLoginException();
		}

		if (account instanceof PersonalAccount) {
			PersonalAccount paccount = (PersonalAccount) account;

			List<Interest> selectedInterests = Lists.newArrayList();
			for (Interest i : paccount.getInterests()) {
				Interest entry = interestDao.findInterestByName(i.getName());
				if (entry != null) {
					selectedInterests.add(entry);
				}
			}
			paccount.getInterests().clear();
			paccount.getInterests().addAll(selectedInterests);
			return accountDao.update(paccount);
		} else if (account instanceof BusinessAccount) {
			BusinessAccount baccount = (BusinessAccount) account;
			return accountDao.update(baccount);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getImageUploadUrl() {
		return blobstoreService.createUploadUrl(UPLOAD_SERVICE_ENDPOINT);
	}

	private Account getLoggedInUserBasedOnCookie() {
		// Do we need to check if session already exists. Probably not?
		Long existingUid = (Long) httpSession.get().getAttribute(
				ZipllyServerConstants.SESSION_ID);
		if (existingUid != null) {
			Session session;
			try {
				session = sessionDao.findSessionByUid(existingUid);
				if (isValidSession(session)) {
					return session.getAccount();
				}
			} catch (NotFoundException e) {
			}
		}
		return null;
	}

	private boolean isValidSession() {
		Long uid = getUidFromCookie();
		try {
			Session session = sessionDao.findSessionByUid(uid);
			return isValidSession(session);
		} catch (NotFoundException e) {
			return false;
		}
	}

	protected boolean isValidSession(Session session) {
		if (session == null) {
			return false;
		}
		return session.getExpireAt().after(new Date());
	}

	protected void storeCookie(Long uid) {
		httpSession.get().setMaxInactiveInterval((int) hoursInMillis);
		httpSession.get().setAttribute(ZipllyServerConstants.SESSION_ID, uid);
	}

	private void clearCookie() {
		httpSession.get().removeAttribute(ZipllyServerConstants.SESSION_ID);
	}

	Long getUidFromCookie() {
		return (Long) httpSession.get().getAttribute(
				ZipllyServerConstants.SESSION_ID);
	}
}
