package com.ziplly.app.client.activities;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;

public interface AccountActivityPresenter<T extends AccountDTO> extends Presenter {
	void save(T account);
	void displayPublicProfile(T account);
	void displayProfile(T account);
	void tweet(TweetDTO tweet);
	void logout();
}
