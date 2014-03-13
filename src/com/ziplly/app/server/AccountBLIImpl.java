package com.ziplly.app.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.restfb.types.User;
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
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountRegistrationDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.PasswordRecoveryDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.facebook.dao.FUserDAOFactory;
import com.ziplly.app.facebook.dao.IFUserDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.AccountRegistration;
import com.ziplly.app.model.AccountRegistration.AccountRegistrationStatus;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.Location;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.PasswordRecovery;
import com.ziplly.app.model.PasswordRecoveryStatus;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettings;
import com.ziplly.app.model.Session;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.EmailServiceImpl.EmailEntity;
import com.ziplly.app.server.oauth.AuthFlowManagerFactory;
import com.ziplly.app.server.oauth.OAuthFlowManager;
import com.ziplly.app.shared.BCrypt;
import com.ziplly.app.shared.EmailTemplate;

public class AccountBLIImpl implements AccountBLI {
	public static final int FREE_TWEET_PER_MONTH_THRESHOLD = 1;
	private static final int BUFFER_SIZE = 512;

	protected final long hoursInMillis = 2 * 60 * 60 * 1000;
	private AccountDAO accountDao;
	private SessionDAO sessionDao;
	private BlobstoreService blobstoreService;
	private BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();

	@Inject
	protected Provider<HttpSession> httpSession;

	@Inject
	protected Provider<HttpServletRequest> request;

	private OAuthConfig authConfig;
	Logger logger = Logger.getLogger(AccountBLIImpl.class.getCanonicalName());

	private AccountRegistrationDAO accountRegistrationDao;
	private TransactionDAO transactionDao;
	private EmailService emailService;
	private PasswordRecoveryDAO passwordRecoveryDao;
	private final GcsService gcsService = GcsServiceFactory
	    .createGcsService(new RetryParams.Builder()
	        .initialRetryDelayMillis(10)
	        .retryMaxAttempts(10)
	        .totalRetryPeriodMillis(15000)
	        .build());

	@Inject
	public AccountBLIImpl(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    TransactionDAO transactionDao,
	    PasswordRecoveryDAO passwordRecoveryDao,
	    EmailService emailService,
	    AccountRegistrationDAO accountRegistrationDao) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.transactionDao = transactionDao;
		this.passwordRecoveryDao = passwordRecoveryDao;
		this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		this.emailService = emailService;
		this.accountRegistrationDao = accountRegistrationDao;
		this.authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name(), getEnvironment());
	}

	@Override
	public Environment getEnvironment() {
		Environment env =
		    (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
		        ? Environment.PROD : Environment.DEVEL;
		return env;
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
		logger.log(Level.INFO, String.format("Found session : %s", session.getId()));
		if (isValidSession(session)) {
			Account account = session.getAccount();
			account.setUid(uid);
			AccountDTO response = EntityUtil.convert(account);
			response.setCurrentLocation(EntityUtil.clone(session.getLocation()));
			return response;
		}
		logger.warning(String.format("Invalid session detected for uid %d", uid));
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
	public AccountDTO register(Account account, boolean saveImage) throws AccountExistsException,
	    InternalError,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException {
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
			throw new AccountExistsException("Account already exists with this email: "
			    + existingAccount.getEmail());
		}

		createDefaultNotificationSettings(account);

		if (account instanceof PersonalAccount) {
			createDefaultPrivacySettings((PersonalAccount) account);
		}

		// Set account status
		AccountStatus status =
		    isEmailVerificationRequired(account) ? AccountStatus.PENDING_ACTIVATION
		        : AccountStatus.ACTIVE;
		account.setStatus(status);

		// create account otherwise
		AccountDTO response = accountDao.save(account);

		// Send email verification.
		if (isEmailVerificationRequired(account)) {
			sendEmailVerification(response);
			return EntityUtil.convert(account);
		} else {
			// login user
			Long uid = doLogin(response);

			// send welcome email
			sendWelcomeEmail(account);

			response.setUid(uid);
			return response;
		}
	}

	private void sendWelcomeEmail(Account account) {
		EmailServiceImpl.Builder builder = new EmailServiceImpl.Builder();
		builder
		    .setRecipientName(account.getName())
		    .setRecipientEmail(account.getEmail())
		    .setSenderName(ZipllyServerConstants.APP_ADMIN_EMAIL_NAME)
		    .setSenderEmail(System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY))
		    .setEmailTemplate(EmailTemplate.WELCOME_REGISTRATION);
		emailService.sendTemplatedEmailFromSender(builder);
	}

	private void sendEmailVerification(AccountDTO account) throws InternalError,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException {
		Preconditions.checkNotNull(account);

		try {
			AccountRegistration ar = accountRegistrationDao.findByEmail(account.getEmail());
			sendEmailVerificationCode(ar.getCode(), account);
			return;
		} catch (NoResultException nre) {
			// First time requesting resend verification code.
		}

		// Create account registration entry for the first time.
		try {
			String hash = getHash(account);
			AccountRegistration ar = new AccountRegistration();
			ar.setCode(hash);
			ar.setTimeCreated(new Date());
			ar.setEmail(account.getEmail());
			ar.setAccountId(account.getAccountId());
			ar.setStatus(AccountRegistrationStatus.UNUSED);
			AccountType atype =
			    (account instanceof PersonalAccountDTO) ? AccountType.PERSONAL : AccountType.BUSINESS;
			ar.setAccountType(atype);
			accountRegistrationDao.save(ar);

			sendEmailVerificationCode(hash, account);

		} catch (Exception e) {
			logger.severe(String.format(
			    "Failed to create Hash for account %s, exception %s",
			    account.getEmail(),
			    e));
			throw new InternalError("Failed to send verification email");
		}
	}

	// Sends the email
	private void
	    sendEmailVerificationCode(String verificationCode, AccountDTO account) throws UnsupportedEncodingException,
	        NoSuchAlgorithmException {

		EmailEntity from = new EmailEntity();
		from.email = System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY);
		from.name = ZipllyServerConstants.APP_ADMIN_EMAIL_NAME;

		EmailEntity to = new EmailEntity();
		to.email = account.getEmail();
		to.name = account.getDisplayName();
		Map<String, String> data = Maps.newHashMap();
		data.put(ZipllyServerConstants.RECIPIENT_NAME_KEY, account.getDisplayName());
		data.put(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, account.getEmail());
		data.put(ZipllyServerConstants.SENDER_NAME_KEY, ZipllyServerConstants.APP_ADMIN_EMAIL_NAME);
		data.put(
		    ZipllyServerConstants.SENDER_EMAIL_KEY,
		    System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY));
		data.put(
		    ZipllyServerConstants.CONFIRM_EMAIL_URL_KEY,
		    getEmailConfirmationUrl(verificationCode, account.getAccountId()));
		emailService.sendTemplatedEmail(from, to, EmailTemplate.EMAIL_VERIFICATION, data);
	}

	/**
	 * Used to copy image from input url to GCS
	 * 
	 * @param istream
	 * @param ostream
	 * @throws IOException
	 */
	private BlobKey saveImage(String url) {
		Preconditions.checkArgument(url != null);
		String fname = UUID.randomUUID().toString();
		String bucketName = System.getProperty(StringConstants.BUCKET_NAME);
		GcsFilename file = new GcsFilename(bucketName, fname);
		try {
			GcsOutputChannel outputChannel =
			    gcsService.createOrReplace(file, GcsFileOptions.getDefaultInstance());

			InputStream istream = new URL(url).openStream();
			copy(istream, Channels.newOutputStream(outputChannel));

			return blobService.createGsBlobKey("/gs/" + bucketName + "/" + file.getObjectName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void copy(InputStream istream, OutputStream ostream) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = istream.read(buffer);
		try {
			while (bytesRead != -1) {
				ostream.write(buffer, 0, bytesRead);
				bytesRead = istream.read(buffer);
			}
		} finally {
			istream.close();
			ostream.close();
		}
	}

	private void createDefaultNotificationSettings(Account account) {
		for (NotificationType type : NotificationType.values()) {
			if (type.isVisible()) {
				AccountNotificationSettings an = new AccountNotificationSettings();
				an.setAccount(account);
				an.setType(type);
				an.setAction(NotificationAction.EMAIL);
				an.setTimeCreated(new Date());
				account.getNotificationSettings().add(an);
			}
		}
	}

	private void createDefaultPrivacySettings(PersonalAccount account) {
		for (AccountDetailsType adt : AccountDetailsType.values()) {
			PrivacySettings as = new PrivacySettings();
			as.setSection(adt);
			as.setSetting(ShareSetting.PUBLIC);
			as.setAccount(account);
			as.setTimeCreated(new Date());
			account.addPrivacySettings(as);
		}
	}

	@Override
	public AccountDTO
	    validateLogin(String email, String password) throws InvalidCredentialsException,
	        NotFoundException {

		AccountDTO account = doValidateLogin(email, password);

		// Throw error if account isn't active
		if (account.getStatus() != AccountStatus.ACTIVE) {
			throw new InvalidCredentialsException();
		}

		// login user
		Long uid = doLogin(account);
		account.setUid(uid);
		return account;
	}

	private AccountDTO
	    doValidateLogin(String email, String password) throws InvalidCredentialsException,
	        NotFoundException {
		AccountDTO account = null;
		try {
			account = accountDao.findByEmail(email);
			String storedPassword = account.getPassword();
			boolean checkpw = BCrypt.checkpw(password, storedPassword);
			if (!checkpw) {
				throw new InvalidCredentialsException("Invalid account credentials");
			}
			// Update last login time.
			account.setLastLoginTime(new Date());
			accountDao.update(EntityUtil.convert(account));
		} catch (NotFoundException e) {
			throw e;
		}

		return account;
	}

	@Override
	public Long doLogin(AccountDTO account) {
		Session session = new Session();
		session.setAccount(EntityUtil.convert(account));
		session.setLocation(new Location(getDefaultLocation(account)));
		Date currTime = new Date();
		Date expireAt = new Date(currTime.getTime() + hoursInMillis);
		session.setExpireAt(expireAt);
		session.setTimeCreated(currTime);
		Long uid = UUID.randomUUID().getMostSignificantBits();
		session.setUid(uid);
		sessionDao.save(session);

		storeCookie(uid);

		// set current location
		account.setCurrentLocation(EntityUtil.clone(session.getLocation()));
		return uid;
	}

	private LocationDTO getDefaultLocation(AccountDTO account) {
		return account.getLocations().get(0);
	}

	@Override
	public void logout(Long uid) throws NotFoundException {
		if (uid == null) {
			throw new IllegalArgumentException();
		}

		Long uidInCookie = getUidFromCookie();
		Long uidInRequest = uid;
		// if (uidInCookie == null || !uidInCookie.equals(uidInRequest)) {
		// throw new IllegalAccessError();
		// }

		Session session = sessionDao.findSessionByUid(uidInRequest);
		if (session == null) {
			logger.log(
			    Level.WARNING,
			    String.format("Session %l exists in cookie but not in session table", uidInCookie));
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

		OAuthFlowManager authFlowManager = AuthFlowManagerFactory.get(authConfig);
		AccessToken token;
		try {
			token = authFlowManager.exchange(code);
		} catch (Exception e) {
			logger.severe(String.format("OAuth failed oauth exchange with exception %s", e));
			throw new OAuthException();
		}
		IFUserDAO fUserDao = FUserDAOFactory.getFUserDao(token.getAccess_token());
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
			Gender gender =
			    fuser.getGender() != null ? Gender.valueOf(fuser.getGender().toUpperCase())
			        : Gender.NOT_SPECIFIED;
			account.setGender(gender);
			account.setIntroduction(fuser.getBio());
//			String imgUrl = "https://graph.facebook.com/" + fuser.getId() + "/picture?type=large"; // ?width=200&height=160
//			account.setImageUrl(imgUrl);
			return account;
		} catch (Exception e) {
			logger.severe("Exception caught while getting facebook user details:" + e);
		}

		// login user
		Long uid = doLogin(response);
		// AccountDTO result = new AccountDTO(response);
		if (response instanceof PersonalAccountDTO) {
			response.setUid(uid);
			return response;
		}

		throw new RuntimeException();
	}

	@Override
	public AccountDTO updateAccount(Account account) throws NeedsLoginException, NotFoundException {
		if (!isValidSession()) {
			throw new NeedsLoginException();
		}

		try {
			accountDao.update(account);
			AccountDTO response = EntityUtil.convert(account);
			Session session = sessionDao.findSessionByAccountId(account.getAccountId());
			response.setCurrentLocation(EntityUtil.clone(session.getLocation()));
			return response;
		} catch (NotFoundException e) {
			logger.severe(String.format(
			    "Failed to retrieve session for account %d",
			    account.getAccountId()));
			throw e;
		}
	}

	@Override
	public String getImageUploadUrl() {

		String bucketName = System.getProperty("gcs_bucket_name");
		UploadOptions options = UploadOptions.Builder.withGoogleStorageBucketName(bucketName);
		String uploadEndpoint = System.getProperty(ZipllyServerConstants.UPLOAD_ENDPOINT);
		String uploadUrl = blobstoreService.createUploadUrl(uploadEndpoint, options);

		String result = uploadUrl;
		logger.log(Level.INFO, String.format("UPLOAD URL SET to %s", uploadUrl));

		// Hack to make this work in dev environment
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			result = uploadUrl.replace("susnatas-MacBook-Pro.local:8888",
			// "susnatas-mbp.bauhauscoffee.net:8888",
			    "127.0.0.1:8888");
		}

		logger.log(Level.INFO, String.format("Upload(RESULT) url set to %s", result));
		return result;

	}

	private Account getLoggedInUserBasedOnCookie() {
		// Do we need to check if session already exists. Probably not?
		Long existingUid = (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
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
		return (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
	}

	@Override
	public TransactionDTO pay(TransactionDTO txn) throws AccountAlreadySubscribedException,
	    DuplicateException,
	    NotFoundException {
		if (txn == null) {
			throw new IllegalArgumentException();
		}

		AccountDTO accountDto = accountDao.findById(txn.getSeller().getAccountId());
		Transaction transaction = new Transaction(txn);
		transaction.setSeller(new Account(accountDto));

		if (!(accountDto instanceof BusinessAccountDTO)) {
			throw new IllegalArgumentException("Invalid account type in PAY()");
		}

		BusinessAccountDTO account = (BusinessAccountDTO) accountDto;
		for (TransactionDTO existingTransaction : account.getTransactions()) {
			if (existingTransaction.getStatus() == TransactionStatus.ACTIVE) {
				throw new DuplicateException("Already has an active subscription");
			}
		}

		try {
			TransactionDTO result = transactionDao.save(transaction);
			return result;
		} catch (DuplicateException e) {
			throw new AccountAlreadySubscribedException();
		}
	}

	@Override
	public void
	    updatePassword(Account account, String oldPassword, String newPassword) throws InvalidCredentialsException,
	        NotFoundException {

		Preconditions.checkArgument(oldPassword != null && newPassword != null && account != null);
		try {
			doValidateLogin(account.getEmail(), oldPassword);
		} catch (InvalidCredentialsException e) {
			throw e;
		} catch (NotFoundException e) {
			// shouldn't get in here
			throw e;
		}
		account.setPassword(newPassword);
		accountDao.updatePassword(account);
	}

	@Override
	public void sendPasswordRecoveryEmail(String email) throws NotFoundException,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException,
	    DuplicateException {
		
		if (email == null) {
			throw new IllegalArgumentException();
		}

		AccountDTO account = accountDao.findByEmail(email);
		String hash = getHash(account);
		passwordRecoveryDao.createOrUpdate(email, hash);

		EmailEntity from = new EmailEntity();
		from.email = System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY);
		from.name = ZipllyServerConstants.APP_ADMIN_EMAIL_NAME;

		EmailEntity to = new EmailEntity();
		to.email = email;
		to.name = account.getDisplayName();
		Map<String, String> data = Maps.newHashMap();
		data.put(ZipllyServerConstants.RECIPIENT_NAME_KEY, account.getDisplayName());
		data.put(ZipllyServerConstants.RECIPIENT_EMAIL_KEY, account.getEmail());
		data.put(ZipllyServerConstants.SENDER_NAME_KEY, ZipllyServerConstants.APP_ADMIN_EMAIL_NAME);
		data.put(
		    ZipllyServerConstants.SENDER_EMAIL_KEY,
		    System.getProperty(ZipllyServerConstants.APP_ADMIN_EMAIL_KEY));
		data.put(ZipllyServerConstants.PASSWORD_RESET_URL_KEY, getPasswordRecoveryUrl(hash));
		emailService.sendTemplatedEmail(from, to, EmailTemplate.PASSWORD_RECOVERY, data);
	}

	private String getEmailConfirmationUrl(String hash, Long accountId) {
		HttpServletRequest httpRequest = request.get();
		return httpRequest.getScheme() + "://" + httpRequest.getServerName()
		    + httpRequest.getContextPath() + "#emailverification:" + hash
		    + StringConstants.URL_PARAMATER_SEPARATOR + accountId;
	}

	private String getPasswordRecoveryUrl(String hash) {
		HttpServletRequest httpRequest = request.get();
		return httpRequest.getScheme() + "://" + httpRequest.getServerName()
		    + httpRequest.getContextPath() + "#passwordrecovery:" + hash;
	}

	@Override
	public AccountDTO verifyPasswordRecoverLink(String hash) throws AccessError, NotFoundException {
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

	private String getHash(AccountDTO account) throws UnsupportedEncodingException,
	    NoSuchAlgorithmException {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		String hash = account.getEmail() + Long.toString(System.currentTimeMillis());
		byte[] encodeBase64 = Base64.encodeBase64(hash.getBytes("UTF-8"));
		return new String(encodeBase64);
	}

	// TODO maintaing transaction???
	@Override
	public void resetPassword(Long accountId, String password) throws NotFoundException,
	    InvalidCredentialsException {
		AccountDTO account = accountDao.findById(accountId);

		if (account.getStatus() != AccountStatus.ACTIVE) {
			throw new InvalidCredentialsException();
		}

		Account acct = EntityUtil.convert(account);
		acct.setPassword(password);
		accountDao.updatePassword(acct);
		PasswordRecovery pr = passwordRecoveryDao.findByEmail(acct.getEmail());
		pr.setStatus(PasswordRecoveryStatus.DONE);
		passwordRecoveryDao.update(pr);
	}

	@Override
	public void resendEmailVerification(String email) throws NotFoundException,
	    AccountExistsException,
	    InternalError,
	    UnsupportedEncodingException,
	    NoSuchAlgorithmException {
		try {
			AccountDTO account = accountDao.findByEmail(email);
			if (account.getStatus() != AccountStatus.PENDING_ACTIVATION) {
				throw new AccountExistsException();
			}

			sendEmailVerification(account);
		} catch (NotFoundException e) {
			logger.severe(String.format(
			    "Error trying to resend email to an non-existing account %s",
			    email));
			throw e;
		}
	}

	/**
	 * No verification is required if coming from Facebook.
	 */
	private boolean isEmailVerificationRequired(Account account) {
		// Email verification required
		String property = System.getProperty(StringConstants.EMAIL_VERIFICATION_FEATURE_FLAG, "true");

		boolean required = Boolean.valueOf(property);
		if (!required) {
			return false;
		}

		if (account instanceof PersonalAccount) {
			boolean isFacebookRegistration = ((PersonalAccount) account).isFacebookRegistration();
			return !isFacebookRegistration;
		}

		return true;
	}
}
