package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.model.AccountDTO;

public interface ISettingsView<T extends AccountDTO> extends View<AccountSettingsPresenter<T>> {
	void displaySettings(T account);
	void onSave();
	void clearError();
	void displayMessage(String msg, AlertType type);
	void setUploadFormActionUrl(String imageUrl);
	void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler);
	void displayImagePreview(String imageUrl);
	void resetUploadForm();
	void onUpload();
	void onCancel();
}
