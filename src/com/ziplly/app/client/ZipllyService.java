package com.ziplly.app.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;

@RemoteServiceRelativePath("zipllyservice")
public interface ZipllyService extends RemoteService {
	/* OAuth 2*/
	AccountDetails getAccessToken(String code) throws Exception;
	
	/* Account */
	AccountDetails getLoggedInUser();
	AccountDetails loginAccountById(long accountId);
	Account getAccountById(long accountId);
	void logoutAccount();

}
