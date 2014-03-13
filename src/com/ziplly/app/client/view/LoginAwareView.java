package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

public interface LoginAwareView {
	void displayLoginErrorMessage(String msg, AlertType type);

	void resetLoginForm();
}
