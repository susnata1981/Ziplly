package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.LoginPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.LoginWidget;

public class LoginAccountView extends AbstractView implements ILoginAccountView<LoginPresenter> {

	private static LoginAccountViewUiBinder uiBinder = GWT.create(LoginAccountViewUiBinder.class);

	interface LoginAccountViewUiBinder extends UiBinder<Widget, LoginAccountView> {
	}

	@UiField
	LoginWidget loginWidget;

	LoginPresenter presenter;

	@Inject
	public LoginAccountView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(LoginPresenter presenter) {
		this.presenter = presenter;
		loginWidget.setPresenter(presenter);
	}

	@Override
	public void onLoad() {
		setBackgroundImage(ZResources.IMPL.neighborhoodLargePic().getSafeUri().asString());
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
