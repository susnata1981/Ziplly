package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.model.AccountDTO;

public interface ISettingsView<K extends AccountDTO, T extends AccountSettingsPresenter<K>> extends
    View<T> {
	void displaySettings(K account);

	void onSave();

	void clearError();

	void displayMessage(String msg, AlertType type);

	void setUploadFormActionUrl(String imageUrl);

	void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler);

	void displayImagePreview(String imageUrl);

	void resetUploadForm();

	void onUpload();

	void onCancel();

	void showSaveButton(boolean show);
}
