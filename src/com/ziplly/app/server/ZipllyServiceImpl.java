package com.ziplly.app.server;


public class ZipllyServiceImpl {
	
}

/*
@Singleton
public class ZipllyServiceImpl extends RemoteServiceServlet implements
		ZipllyService {
	private static final long serialVersionUID = 1L;
	private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	private Logger logger = Logger.getLogger("ZipplyServiceImpl");
	@Inject
	private AccountDAO accountDAO;// = new AccountDAOImpl(null);

	@Override
	public AccountDTO getFacebookUserDetails(String code) throws Exception {
		OAuthFlowManager authFlowManager = AuthFlowManagerFactory
				.get(authConfig);
		AccessToken token = authFlowManager.exchange(code);
		IFUserDAO fUserDao = FUserDAOFactory.getFUserDao(token
				.getAccess_token());
		User fuser = fUserDao.getUser();
//		LatLong locInfo = fUserDao.getLocationInfo(fuser);

		if (fuser == null) {
			return null;
		}

		Account response = null;
		try {
			response = accountDAO.findByEmail(fuser.getEmail());
		} catch (NotFoundException nfe) {
			System.out.println("didn't find account.");
			// create user
			AccountDTO account = new AccountDTO();
			account.setEmail(fuser.getEmail());
//			account.setFirstName(fuser.getFirstName());
//			account.setLastName(fuser.getLastName());
//			account.setAccessToken(token.getAccess_token());
//			account.setfId(fuser.getId());
//			account.setLocation(fuser.getLocation().getName());
//			account.setUrl(fuser.getLink());
//			account.setLongitude(locInfo.longitude);
//			account.setLatitude(locInfo.latitude);
//			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId()
					+ "/picture"; //?width=200&height=160
			account.setImageUrl(imgUrl);
			System.out.println("Returning "+account);
			return account;
		} catch (Exception e) {
			System.out
					.println("Exception caught while getting facebook user details:"
							+ e);
			throw e;
		}
		System.out.println("Returning existing account:"+response);
		return new AccountDTO(response);
	}

	@Override
	public AccountDTO loginOrRegisterAccount(String code) throws Exception {
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
		Account response = null;
		try {
			response = accountDAO.findByEmail(fuser.getEmail());
		} catch (NotFoundException nfe) {
			// create user
			account = new AccountDTO();
			account.setEmail(fuser.getEmail());
//			account.setFirstName(fuser.getFirstName());
//			account.setLastName(fuser.getLastName());
//			account.setAccessToken(token.getAccess_token());
//			account.setfId(fuser.getId());
//			account.setLocation(fuser.getLocation().getName());
//			account.setUrl(fuser.getLink());
//			account.setLongitude(locInfo.longitude);
//			account.setLatitude(locInfo.latitude);
//			account.setLastLoginTime(new Date());
//			account.setTimeCreated(new Date());
//			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId()
					+ "/picture?width=200&height=160";
			account.setImageUrl(imgUrl);
			// save
			AccountDTO result = register(account);
			System.out.println("registered account:" + result+" last_login_time="+result.getLastLoginTime());
			return result;
		} catch (Exception e) {
			System.out.println("Exception caught:" + e);
			throw e;
		}
		
		// update access token for existing user
		response.setAccessToken(token.getAccess_token());
		response.setLastLoginTime(new Date());
		accountDAO.update(response);
		return new AccountDTO(response);
	}

	@Override
	public AccountDTO register(AccountDTO newAccount) {
		Account result = null;
		try {
			result = accountDAO.findByEmail(newAccount.getEmail());
		} catch (NotFoundException nfe) {
			// save
			result = new Account(newAccount);
			accountDAO.save(result);
			logger.log(Level.INFO, "Registered account:"+result);
		}
		// throw new
		// IllegalArgumentException("Account:"+newAccount.getDisplayName()+" doens't exists");
		storeUserInSession(newAccount);
		return newAccount;
	}

	private void storeUserInSession(AccountDTO newAccount) {
		HttpSession session = getThreadLocalRequest().getSession();
		session.setMaxInactiveInterval(10*60);
		session.setAttribute(LOGGED_IN_USER, newAccount);
	}

	// TODO change the input param
//	protected AccountDetails saveAccountPreferences(Account account,
//			List<Category> input) {
//
//		return null;
//	}

	@Override
	public AccountDTO doLogin(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TweetDTO> getTweets(AccountDTO a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountDTO getLoggedInUser() {
		HttpSession session = getThreadLocalRequest().getSession();
		return (AccountDTO) session.getAttribute(LOGGED_IN_USER);
	}

	@Override
	public void logoutAccount() {
		HttpSession session = getThreadLocalRequest().getSession();
		session.removeAttribute(LOGGED_IN_USER);
	}

	@Override
	public AccountDTO loginAccountById(long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountDTO getAccountById(long accountId) throws NotFoundException {
		Account response = accountDAO.findById(accountId);
		return new AccountDTO(response);
	}

//	@Override
//	public AccountDetails getAccessToken(String code) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	@Override
	public String getUploadUrl() {
		return blobstoreService.createUploadUrl("/upload");
	}
}
*/