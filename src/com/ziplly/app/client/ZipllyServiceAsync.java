package com.ziplly.app.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.TweetDTO;

public interface ZipllyServiceAsync {
	void getAccessToken(String code, AsyncCallback<AccountDetails> callback);
	void getLoggedInUser(AsyncCallback<AccountDTO> callback);
	void loginAccountById(long accountId, AsyncCallback<AccountDTO> callback);
	void getAccountById(long accountId, AsyncCallback<AccountDTO> callback);
	void logoutAccount(AsyncCallback<Void> callback);
	void doLogin(String code, AsyncCallback<AccountDTO> callback);
	void register(AccountDTO a, AsyncCallback<AccountDTO> callback);
	void getTweets(AccountDTO a, AsyncCallback<List<TweetDTO>> callback);
	void loginOrRegisterAccount(String code, AsyncCallback<AccountDTO> callback);
	void getFacebookUserDetails(String code, AsyncCallback<AccountDTO> callback);
}
