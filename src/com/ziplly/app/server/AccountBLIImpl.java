package com.ziplly.app.server;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.restfb.types.User;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountAlreadySubscribedException;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.OAuthException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.InterestDAO;
import com.ziplly.app.dao.PasswordRecoveryDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.facebook.dao.FUserDAOFactory;
import com.ziplly.app.facebook.dao.IFUserDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSetting;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.PasswordRecovery;
import com.ziplly.app.model.PasswordRecoveryStatus;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.Session;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.server.oauth.AuthFlowManagerFactory;
import com.ziplly.app.server.oauth.OAuthFlowManager;
import com.ziplly.app.shared.BCrypt;
import com.ziplly.app.shared.EmailTemplate;

public class AccountBLIImpl implements AccountBLI {
	private static final String UPLOAD_SERVICE_ENDPOINT = "/upload";
	protected final long hoursInMillis = 2 * 60 * 60 * 1000;
	private AccountDAO accountDao;
	private SessionDAO sessionDao;
	private BlobstoreService blobstoreService;

	@Inject
	protected Provider<HttpSession> httpSession;

	@Inject
	protected Provider<HttpServletRequest> request;

	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	Logger logger = Logger.getLogger(AccountBLIImpl.class.getCanonicalName());

	private InterestDAO interestDao;
	private TransactionDAO transactionDao;
	private SubscriptionPlanDAO subscriptionPlanDao;
	private EmailService emailService;
	private PasswordRecoveryDAO passwordRecoveryDao;

	@Inject
	public AccountBLIImpl(AccountDAO accountDao, SessionDAO sessionDao,
			InterestDAO interestDao, TransactionDAO transactionDao,
			SubscriptionPlanDAO subscriptionPlanDao,
			PasswordRecoveryDAO passwordRecoveryDao, EmailService emailService) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.interestDao = interestDao;
		this.transactionDao = transactionDao;
		this.subscriptionPlanDao = subscriptionPlanDao;
		this.passwordRecoveryDao = passwordRecoveryDao;
		this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		this.emailService = emailService;
		// createInterestEntries();
		// createSubscriptionPlan();
	}

	private void createSubscriptionPlan() {
		SubscriptionPlan plan = new SubscriptionPlan();
		plan.setName("Basic plan");
		plan.setDescription("Send upto 5 messages a month");
		plan.setFee(5.00);
		plan.setTimeCreated(new Date());
		subscriptionPlanDao.save(plan);
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
	public AccountDTO getLoggedInUser() throws NotFoundException {
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
			return EntityUtil.convert(account);
		}
		System.out.println("Invalid session....");
		return null;
	}

	@Override
	public AccountDTO getAccountById(Long accountId) throws NotFoundException {
		if (accountId == null) {
			throw new IllegalArgumentException();
		}
		return accountDao.findById(accountId);
	}

	@Override
	public AccountDTO register(Account account) throws AccountExistsException {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		// check for existing account
		AccountDTO existingAccount = null;
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
		AccountDTO response = accountDao.save(account);

		// login user
		Long uid = doLogin(response);

		// send welcome email
		emailService.sendEmail(account.getName(), account.getEmail(),
				EmailTemplate.WELCOME_REGISTRATION);
		response.setUid(uid);
		return response;
	}

	private void createDefaultAccountSettings(PersonalAccount account) {
		for (AccountDetailsType adt : AccountDetailsType.values()) {
			AccountSetting as = new AccountSetting();
			as.setSection(adt);
			as.setSetting(ShareSetting.PUBLIC);
			// as.setAccount(account);
			account.getAccountSettings().add(as);
		}
	}

	@Override
	public AccountDTO validateLogin(String email, String password)
			throws InvalidCredentialsException, NotFoundException {

		AccountDTO account = doValidateLogin(email, password);
		// login user
		Long uid = doLogin(account);
		account.setUid(uid);
		return account;
	}

	private AccountDTO doValidateLogin(String email, String password)
			throws InvalidCredentialsException, NotFoundException {
		AccountDTO account = null;
		try {
			account = accountDao.findByEmail(email);
			String storedPassword = account.getPassword();
			boolean checkpw = BCrypt.checkpw(password, storedPassword);
			if (!checkpw) {
				throw new InvalidCredentialsException(
						"Invalid account credentials");
			}
		} catch (NotFoundException e) {
			throw e;
		}
		return account;
	}

	@Override
	public Long doLogin(AccountDTO account) {
		Session session = new Session();
		session.setAccount(EntityUtil.convert(account));
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
			return EntityUtil.convert(loggedInAccount);
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

		AccountDTO response = null;
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
		// AccountDTO result = new AccountDTO(response);
		if (response instanceof PersonalAccountDTO) {
			// PersonalAccountDTO result = new
			// PersonalAccountDTO((PersonalAccount)response);
			response.setUid(uid);
			System.out.println("Returning existing account:" + response);
			return response;
		}

		throw new RuntimeException();
	}

	@Override
	public AccountDTO updateAccount(Account account) throws NeedsLoginException {
		if (!isValidSession()) {
			throw new NeedsLoginException();
		}
		AccountDTO response = accountDao.update(account);
		return response;
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

	@Override
	public List<PersonalAccountDTO> getAccountByZip(AccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		return accountDao.findByZip(account.getZip());
	}

	@Override
	public TransactionDTO pay(TransactionDTO txn)
			throws AccountAlreadySubscribedException {
		if (txn == null) {
			throw new IllegalArgumentException();
		}
		Transaction transaction = new Transaction(txn);
		try {
			TransactionDTO result = transactionDao.save(transaction);
			return result;
		} catch (DuplicateException e) {
			throw new AccountAlreadySubscribedException();
		}
	}

	@Override
	public void updatePassword(Account account, String oldPassword,
			String newPassword) throws InvalidCredentialsException,
			NotFoundException {

		if (oldPassword == null || newPassword == null || account == null) {
			throw new IllegalArgumentException();
		}

		try {
			doValidateLogin(account.getEmail(), oldPassword);
		} catch (InvalidCredentialsException e) {
			throw e;
		} catch (NotFoundException e) {
			// shouldn't get in here
			e.printStackTrace();
			throw e;
		}
		account.setPassword(newPassword);
		accountDao.update(account);
	}

	@Override
	public void sendPasswordRecoveryEmail(String email)
			throws NotFoundException, UnsupportedEncodingException,
			NoSuchAlgorithmException, DuplicateException {
		if (email == null) {
			throw new IllegalArgumentException();
		}

		try {
			AccountDTO account = accountDao.findByEmail(email);
			String hash = getHash(account.getEmail() + new Date().toString());
			PasswordRecovery pr = new PasswordRecovery();
			pr.setEmail(account.getEmail());
			pr.setHash(hash);
			pr.setTimeCreated(new Date());
			pr.setStatus(PasswordRecoveryStatus.PENDING);
			passwordRecoveryDao.save(pr);

			// send email
			Map<String, String> emailData = Maps.newHashMap();
			emailData.put(StringConstants.RECIPIENT_EMAIL, account.getEmail());
			emailData.put(StringConstants.RECIPIENT_NAME,
					account.getDisplayName());
			String passwordRecoveryUrl = getPasswordRecoveryUrl(hash);
			System.out
					.println("Password recovery url = " + passwordRecoveryUrl);
			emailData.put(StringConstants.PASSWORD_RECOVER_URL,
					passwordRecoveryUrl);
			emailService.sendEmail(emailData, EmailTemplate.PASSWORD_RECOVERY);

		} catch (NotFoundException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (NoSuchAlgorithmException nsa) {
			throw nsa;
		}
	}

	@Override
	public AccountDTO verifyPasswordRecoverLink(String hash)
			throws AccessError, NotFoundException {
		if (hash == null) {
			throw new IllegalArgumentException();
		}
		PasswordRecovery pr = null;
		try {
			pr = passwordRecoveryDao.findByHash(hash);
		} catch (NoResultException nre) {
			throw nre;
		}
		
		if (pr != null) {
			if (pr.getStatus() != PasswordRecoveryStatus.PENDING) {
				throw new AccessError();
			}
			AccountDTO account = accountDao.findByEmail(pr.getEmail());
			return account;
		}

		return null;
	}

	private String getHash(String input) throws UnsupportedEncodingException,
			NoSuchAlgorithmException {
		if (input == null) {
			throw new IllegalArgumentException();
		}
		byte[] encodeBase64 = Base64.encodeBase64(input.getBytes("UTF-8"));
		return new String(encodeBase64);
	}

	private String getPasswordRecoveryUrl(String hash) {
		String passwordRecoveryUrl = "";
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			HttpServletRequest req = request.get();
			passwordRecoveryUrl = req.getScheme() + "://" + req.getServerName()
					+ req.getContextPath() + ":8888/Ziplly.html"
					+ "#passwordrecovery:" + hash;
		} else {

		}
		return passwordRecoveryUrl;
	}

	// TODO maintaing transaction???
	@Override
	public void resetPassword(Long accountId, String password)
			throws NotFoundException {
		try {
			AccountDTO account = accountDao.findById(accountId);
			Account acct = EntityUtil.convert(account);
			acct.setPassword(password);
			accountDao.updatePassword(acct);
			PasswordRecovery pr = passwordRecoveryDao.findByEmail(acct
					.getEmail());
			pr.setStatus(PasswordRecoveryStatus.DONE);
			passwordRecoveryDao.update(pr);
		} catch (NotFoundException e) {
			throw e;
		}
	}
}
