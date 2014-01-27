package com.ziplly.app.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;

@RemoteServiceRelativePath("zipllyservice")
public interface ZipllyService extends RemoteService {
	/* OAuth 2*/
	//	AccountDetails getAccessToken(String code) throws Exception;
	
	/* Account */
	AccountDTO getLoggedInUser();
	AccountDTO loginAccountById(long accountId);
	AccountDTO getAccountById(long accountId) throws NotFoundException;
	AccountDTO register(AccountDTO a);
	AccountDTO loginOrRegisterAccount(String code) throws Exception;
	AccountDTO doLogin(String code);

	void logoutAccount();
	List<TweetDTO> getTweets(AccountDTO a);

	AccountDTO getFacebookUserDetails(String code) throws Exception;
	String getUploadUrl();
}
