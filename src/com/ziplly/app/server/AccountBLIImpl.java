package com.ziplly.app.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
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

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
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
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.BusinessAccountDTO;
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
	private ImagesService imageService = ImagesServiceFactory.getImagesService();
	private BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();

	@Inject
	protected Provider<HttpSession> httpSession;

	@Inject
	protected Provider<HttpServletRequest> request;

	private OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());
	Logger logger = Logger.getLogger(AccountBLIImpl.class.getCanonicalName());

	private InterestDAO interestDao;
	private TransactionDAO transactionDao;
	private SubscriptionPlanDAO subscriptionPlanDao;
	private EmailService emailService;
	private PasswordRecoveryDAO passwordRecoveryDao;
	private final GcsService gcsService = GcsServiceFactory
			.createGcsService(new RetryParams.Builder().initialRetryDelayMillis(10)
					.retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());

	@Inject
	public AccountBLIImpl(AccountDAO accountDao, SessionDAO sessionDao, InterestDAO interestDao,
			TransactionDAO transactionDao, SubscriptionPlanDAO subscriptionPlanDao,
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

//	private void createSubscriptionPlan() {
//		SubscriptionPlan plan = new SubscriptionPlan();
//		plan.setName("Basic plan");
//		plan.setDescription("Send upto 3 messages a month");
//		plan.setTweetsAllowed(3);
//		plan.setFee(0.0);
//		plan.setTimeCreated(new Date());
//		subscriptionPlanDao.save(plan);
//
//		plan = new SubscriptionPlan();
//		plan.setName("Pro plan");
//		plan.setDescription("Send upto 6 messages a month");
//		plan.setTweetsAllowed(6);
//		plan.setFee(5.00);
//		plan.setTimeCreated(new Date());
//		subscriptionPlanDao.save(plan);
//
//		plan = new SubscriptionPlan();
//		plan.setName("Premium plan");
//		plan.setDescription("Send upto 15 messages a month");
//		plan.setTweetsAllowed(15);
//		plan.setFee(15.00);
//		plan.setTimeCreated(new Date());
//		subscriptionPlanDao.save(plan);
//	}
//
//	private void createInterestEntries() {
//		for (Activity a : Activity.values()) {
//			Interest i = new Interest();
//			i.setName(a.name().toLowerCase());
//			i.setTimeCreated(new Date());
//			interestDao.save(i);
//		}
//	}

	// TODO should we throw exception for invalid attempt?
	@Override
	public AccountDTO getLoggedInUser() throws NotFoundException {
		Long uid = getUidFromCookie();

		if (uid == null) {
			return null;
		}
		Session session = null;
		session = sessionDao.findSessionByUid(uid);
		logger.log(Level.INFO, String.format("Found session : %s",session.getId()));
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
	public AccountDTO register(Account account, boolean saveImage) throws AccountExistsException {
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

		if (saveImage && account.getImageUrl() != null) {
			BlobKey blobKey = saveImage(account.getImageUrl());
			String servingUrl = imageService.getServingUrl(ServingUrlOptions.Builder
					.withBlobKey(blobKey));
			account.setImageUrl(servingUrl);
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
			GcsOutputChannel outputChannel = gcsService.createOrReplace(file,
					GcsFileOptions.getDefaultInstance());

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
				throw new InvalidCredentialsException("Invalid account credentials");
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
					"Session %l exists in cookie but not in session table", uidInCookie));
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
			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId() + "/picture?type=large"; // ?width=200&height=160
			account.setImageUrl(imgUrl);
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
	public AccountDTO updateAccount(Account account) throws NeedsLoginException {
		if (!isValidSession()) {
			throw new NeedsLoginException();
		}
		AccountDTO response = accountDao.update(account);
		return response;
	}

	@Override
	public String getImageUploadUrl() {

		String bucketName = System.getProperty("gcs_bucket_name");
		UploadOptions options = UploadOptions.Builder.withGoogleStorageBucketName(bucketName);
		String uploadEndpoint = System.getProperty(StringConstants.UPLOAD_ENDPOINT);
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
	public List<PersonalAccountDTO> getAccountByZip(AccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}

		return accountDao.findByZip(account.getZip());
	}

	@Override
	public TransactionDTO pay(TransactionDTO txn) throws AccountAlreadySubscribedException,
			DuplicateException, NotFoundException {
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
	public void updatePassword(Account account, String oldPassword, String newPassword)
			throws InvalidCredentialsException, NotFoundException {

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
			UnsupportedEncodingException, NoSuchAlgorithmException, DuplicateException {
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
			emailData.put(StringConstants.RECIPIENT_NAME_KEY, account.getDisplayName());
			String passwordRecoveryUrl = getPasswordRecoveryUrl(hash);
			System.out.println("Password recovery url = " + passwordRecoveryUrl);
			emailData.put(StringConstants.PASSWORD_RECOVER_URL, passwordRecoveryUrl);
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
					+ req.getContextPath() + ":8888/Ziplly.html" + "#passwordrecovery:" + hash;
		} else {

		}
		return passwordRecoveryUrl;
	}

	// TODO maintaing transaction???
	@Override
	public void resetPassword(Long accountId, String password) throws NotFoundException {
		try {
			AccountDTO account = accountDao.findById(accountId);
			Account acct = EntityUtil.convert(account);
			acct.setPassword(password);
			accountDao.updatePassword(acct);
			PasswordRecovery pr = passwordRecoveryDao.findByEmail(acct.getEmail());
			pr.setStatus(PasswordRecoveryStatus.DONE);
			passwordRecoveryDao.update(pr);
		} catch (NotFoundException e) {
			throw e;
		}
	}

	// TODO(shaan) refactor
	@Override
	public void sendEmailByZip(Account sender, Long tweetId, NotificationType type,
			EmailTemplate template) {
		Queue queue = QueueFactory.getQueue(StringConstants.EMAIL_QUEUE_NAME);
		String backendAddress = BackendServiceFactory.getBackendService().getBackendAddress(
				System.getProperty(StringConstants.BACKEND_INSTANCE_NAME_1));
		String mailEndpoint = System.getProperty(StringConstants.MAIL_ENDPOINT);

		TaskOptions options = TaskOptions.Builder.withUrl(mailEndpoint).method(Method.POST)
				.param("action", EmailAction.BY_ZIP.name())
				.param("senderAccountId", sender.getAccountId().toString())
				.param("tweetId", tweetId.toString()).param("notificationType", type.name())
				.param("zip", Integer.toString(sender.getZip()))
				.param("emailTemplateId", template.name()).header("Host", backendAddress);
		queue.add(options);
	}

	@Override
	public void sendEmail(Account sender, Account receiver, EmailTemplate template) {
		Queue queue = QueueFactory.getQueue(StringConstants.EMAIL_QUEUE_NAME);
		String backendAddress = BackendServiceFactory.getBackendService().getBackendAddress(
				System.getProperty(StringConstants.BACKEND_INSTANCE_NAME_1));
		String mailEndpoint = System.getProperty(StringConstants.MAIL_ENDPOINT);

		TaskOptions options = TaskOptions.Builder.withUrl(mailEndpoint).method(Method.POST)
				.param("action", EmailAction.INDIVIDUAL.name())
				.param("recipientEmail", receiver.getEmail())
				.param("recipientName", receiver.getName())
				.param("emailTemplateId", EmailTemplate.WELCOME_REGISTRATION.name())
				.header("Host", backendAddress);
		queue.add(options);

	}
}
