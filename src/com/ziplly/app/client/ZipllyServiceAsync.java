package com.ziplly.app.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;

public interface ZipllyServiceAsync {
	void getAccessToken(String code, AsyncCallback<AccountDetails> callback);
	void getLoggedInUser(AsyncCallback<AccountDTO> callback);
	void loginAccountById(long accountId, AsyncCallback<AccountDTO> callback);
	void getAccountById(long accountId, AsyncCallback<AccountDTO> callback);
	void logoutAccount(AsyncCallback<Void> callback);
	void doLogin(String code, AsyncCallback<AccountDTO> callback);
	void register(AccountDTO a, AsyncCallback<AccountDTO> callback);
}
