package com.ziplly.app.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDetails;

public interface ZipllyServiceAsync {
	void getAccessToken(String code, AsyncCallback<AccountDetails> callback);
	void getLoggedInUser(AsyncCallback<AccountDetails> callback);
	void loginAccountById(long accountId, AsyncCallback<AccountDetails> callback);
	void getAccountById(long accountId, AsyncCallback<Account> callback);
	void logoutAccount(AsyncCallback<Void> callback);
}
