package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.model.AccountDTO;

public interface IAccountViewRenderer<T extends AccountDTO> {
	void displayProfile(T account);
	void displayPublicProfile(T account);
	void displayLoginWidget();
	void displayLogoutWigdet();
	IAccountView<T> getWidget();
	void clear();
	void displayAccountUpdateFailedMessage();
	void displayAccountUpdateSuccessfullMessage();
	void displayLoginErrorMessage(String invalidAccountCredentials,
			AlertType error);
	void resetLoginForm();
	void setPresenter(AccountActivity2 accountActivity);
}
