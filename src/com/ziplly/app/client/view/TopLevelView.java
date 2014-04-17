package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;

public interface TopLevelView {
	void displayModalMessage(String message, AlertType type);
	void displayMessage(String message, AlertType type);
}
