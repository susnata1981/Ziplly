package com.ziplly.app.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessAccountView extends Composite implements IAccountView<BusinessAccountDTO> {

	private static BusinessAccountViewUiBinder uiBinder = GWT
			.create(BusinessAccountViewUiBinder.class);

	interface BusinessAccountViewUiBinder extends
			UiBinder<Widget, BusinessAccountView> {
	}

	private Object presenter;

	public BusinessAccountView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPresenter(AccountActivityPresenter presenter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayLoginWidget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayLogoutWidget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayProfile(BusinessAccountDTO account) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayPublicProfile(BusinessAccountDTO account) {
		// TODO Auto-generated method stub
		
	}
}
