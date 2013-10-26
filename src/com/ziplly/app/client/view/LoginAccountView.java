package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.LoginPresenter;
import com.ziplly.app.client.widget.LoginWidget;

public class LoginAccountView extends Composite implements ILoginAccountView<LoginPresenter> {

	private static LoginAccountViewUiBinder uiBinder = GWT
			.create(LoginAccountViewUiBinder.class);

	interface LoginAccountViewUiBinder extends
			UiBinder<Widget, LoginAccountView> {
	}

	@UiField
	LoginWidget loginWidget;
	
	LoginPresenter presenter;
	
	public LoginAccountView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(LoginPresenter presenter) {
		this.presenter = presenter;
		loginWidget.setPresenter(presenter);
	}

	@Override
	public void clear() {
		loginWidget.clear();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		loginWidget.displayMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		loginWidget.resetLoginForm();
	}

	@Override
	public void resetMessage() {
		loginWidget.resetMessage();
	}
}
