package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.restfb.types.User;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.oauth.AccessToken;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.facebook.dao.FUserDAOFactory;
import com.ziplly.app.facebook.dao.IFUserDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.LatLong;
import com.ziplly.app.server.oauth.AuthFlowManagerFactory;
import com.ziplly.app.server.oauth.OAuthFlowManager;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookDetailsResult;

public class GetFacebookDetailsHandler implements ActionHandler<GetFacebookDetailsAction, GetFacebookDetailsResult>{
	private OAuthConfig authConfig = OAuthFactory
			.getAuthConfig(OAuthProvider.FACEBOOK.name());
	private AccountDAO accountDao;

	@Inject
	public GetFacebookDetailsHandler(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public GetFacebookDetailsResult execute(GetFacebookDetailsAction input,
			ExecutionContext context) throws DispatchException {
		
		System.out.println("Inside getfacebookdetailshandler...");
		OAuthFlowManager authFlowManager = AuthFlowManagerFactory
				.get(authConfig);
		AccessToken token;
		try {
			token = authFlowManager.exchange(input.getCode());
		} catch (Exception e) {
			throw new ActionException(e.getMessage());
		}
		IFUserDAO fUserDao = FUserDAOFactory.getFUserDao(token
				.getAccess_token());
		User fuser = fUserDao.getUser();
		LatLong locInfo = fUserDao.getLocationInfo(fuser);

		if (fuser == null) {
			return null;
		}

		Account response = null;
		try {
			response = accountDao.findByEmail(fuser.getEmail());
		} catch (NotFoundException nfe) {
			System.out.println("didn't find account.");
			// create user
			AccountDTO account = new AccountDTO();
			account.setEmail(fuser.getEmail());
			account.setFirstName(fuser.getFirstName());
			account.setLastName(fuser.getLastName());
			account.setAccessToken(token.getAccess_token());
			account.setfId(fuser.getId());
			account.setLocation(fuser.getLocation().getName());
			account.setUrl(fuser.getLink());
			account.setLongitude(locInfo.longitude);
			account.setLatitude(locInfo.latitude);
			account.setIntroduction(fuser.getBio());
			String imgUrl = "https://graph.facebook.com/" + fuser.getId()
					+ "/picture"; //?width=200&height=160
			account.setImageUrl(imgUrl);
			System.out.println("Returning "+account);
			return new GetFacebookDetailsResult(account);
		} catch (Exception e) {
			System.out
					.println("Exception caught while getting facebook user details:"
							+ e);
		}
		System.out.println("Returning existing account:"+response);
		return new GetFacebookDetailsResult(new AccountDTO(response));
	}

	@Override
	public Class<GetFacebookDetailsAction> getActionType() {
		return GetFacebookDetailsAction.class;
	}

	@Override
	public void rollback(GetFacebookDetailsAction arg0,
			GetFacebookDetailsResult arg1, ExecutionContext arg2)
			throws DispatchException {
		// TODO Auto-generated method stub
		
	}

}
