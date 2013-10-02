package com.ziplly.app.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.TweetDTO;

@RemoteServiceRelativePath("zipllyservice")
public interface ZipllyService extends RemoteService {
	/* OAuth 2*/
	AccountDetails getAccessToken(String code) throws Exception;
	
	/* Account */
	AccountDTO getLoggedInUser();
	AccountDTO loginAccountById(long accountId);
	AccountDTO getAccountById(long accountId);
	void logoutAccount();

	AccountDTO doLogin(String code);

	AccountDTO register(AccountDTO a);

	List<TweetDTO> getTweets(AccountDTO a);

}
