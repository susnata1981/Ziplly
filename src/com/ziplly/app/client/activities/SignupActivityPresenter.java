package com.ziplly.app.client.activities;

import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.NeighborhoodDTO;

public interface SignupActivityPresenter extends Presenter {
	// void onFacebookLogin();
	void register(AccountDTO account);

	// void setImageUploadUrl();
	// void setUploadImageHandler();
	void register(AccountDTO account, String code);

	void getNeighborhoodData(String postalCode);

	void addToInviteList(String email, int zip);

	void deleteImage(String profileImageUrl);

	Environment getEnvironment();

	void getNeighborhoodData(NeighborhoodDTO n);

	void createNeighborhood(NeighborhoodDTO n);

	void getNeighborhoodList(NeighborhoodDTO parentNeighborhood);

	void setCurrentNeighborhood(NeighborhoodDTO selectedNeighborhood);
}
