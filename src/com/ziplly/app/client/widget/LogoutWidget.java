package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.AccountDTO;

public class LogoutWidget extends Composite {

	private static LogoutWidgetUiBinder uiBinder = GWT
			.create(LogoutWidgetUiBinder.class);

	interface LogoutWidgetUiBinder extends UiBinder<Widget, LogoutWidget> {
	}

//	private OAuthConfig authConfig = OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name());

	@UiField
	Button logoutBtn;
	private AccountPresenter<? extends AccountDTO> presenter;

	@Inject
	public LogoutWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("logoutBtn")
	void logout(ClickEvent event) {
		if (presenter != null) {
			presenter.logout();
		}
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	public void setPresenter(AccountPresenter<? extends AccountDTO> presenter) {
		this.presenter = presenter;
	}
}
