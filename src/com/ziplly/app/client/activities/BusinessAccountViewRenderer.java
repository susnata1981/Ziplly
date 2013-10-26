package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.model.BusinessAccountDTO;

/*
 * Personal account renderer
 */
public class BusinessAccountViewRenderer extends AbstractAccountViewRenderer<BusinessAccountDTO> {

	public BusinessAccountViewRenderer(IAccountView<BusinessAccountDTO> view) {
		super(view);
	}

	@Override
	public void displayProfile(BusinessAccountDTO account) {
		this.view.displayProfile(account);
	}

	@Override
	public void displayLoginWidget() {
		this.view.displayLoginWidget();
	}

	@Override
	public void displayLogoutWigdet() {
		this.view.displayLogoutWidget();
	}
	
	public IAccountView<BusinessAccountDTO> getWidget() {
		return view;
	}

	@Override
	public void clear() {
		this.view.clear();
	}

	@Override
	public void displayAccountUpdateFailedMessage() {
		view.displayAccountUpdateFailedMessage();
	}

	@Override
	public void displayAccountUpdateSuccessfullMessage() {
		view.displayAccountUpdateSuccessfullMessage();
	}

	@Override
	public void displayLoginErrorMessage(String msg, AlertType type) {
		view.displayLoginErrorMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		view.resetLoginForm();
	}

	@Override
	public void setPresenter(AccountActivity2 accountActivity) {
		view.setPresenter(accountActivity);
	}

	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		view.displayPublicProfile(account);
	}
}
