package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.AccountDTO;

public interface EditAccount<T extends AccountDTO> {
	void display(T account);
	boolean validate();
	void cancel(ClickEvent event);
	void displayMessage(String msg, AlertType type);
	void clearError();
	void setPresenter(AccountPresenter<T> presenter);
	void save(ClickEvent event);
	void hide();
	void show();
}
