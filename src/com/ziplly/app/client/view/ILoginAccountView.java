package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.activities.LoginPresenter;

public interface ILoginAccountView<T extends LoginPresenter> extends View<T> {
	void displayMessage(String msg, AlertType type);

	void resetLoginForm();

	void clear();

	void resetMessage();
}
