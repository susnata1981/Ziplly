package com.ziplly.app.client.view;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.AccountDTO;

public interface IAccountView<T extends AccountDTO> extends View<AccountPresenter<T>> {

	void displayProfile(T account);
	
	void displayPublicProfile(T account);
	
	void displayAccountUpdateSuccessfullMessage();
	
	void displayAccountUpdateFailedMessage();
	
	void clearTweet();
	
	void displayLogoutWidget();

	void setImageUploadUrl(String url);

	void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler);

	void displayProfileImagePreview(String imageUrl);

	void resetUploadForm();
}
