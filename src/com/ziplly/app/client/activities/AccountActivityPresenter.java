package com.ziplly.app.client.activities;

import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;

public interface AccountActivityPresenter extends Presenter, LoginWidgetPresenter {
	void save(PersonalAccountDTO account);
	void displayProfile(PersonalAccountDTO account);
	void displayPublicProfile(PersonalAccountDTO account);
	void displayPublicProfile(Long accountId);
	void tweet(TweetDTO tweet);
	void logout();
}
