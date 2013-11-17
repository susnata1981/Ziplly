package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.HomePresenter2;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.PublicAccountPlace;
import com.ziplly.app.client.view.HomeView.HomePresenter;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessAccountWidgetModal extends Composite implements IAccountWidgetModal<BusinessAccountDTO> {

	private static BusinessAccountWidgetModalUiBinder uiBinder = GWT
			.create(BusinessAccountWidgetModalUiBinder.class);

	interface BusinessAccountWidgetModalUiBinder extends
			UiBinder<Widget, BusinessAccountWidgetModal> {
	}

	@UiField
	Modal accountWidgetModal;
	
	@UiField
	Image profileImageUrl;

	@UiField
	Element name;

	@UiField
	Button viewProfileBtn;
	
	@UiField
	Button cancelBtn;

	private HomePresenter presenter;

	private BusinessAccountDTO account;
	
	public BusinessAccountWidgetModal() {
		initWidget(uiBinder.createAndBindUi(this));
		hide();
	}

	private void setupHandlers() {
		viewProfileBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new BusinessAccountPlace(account.getAccountId()));
				hide();
			}
		});
		
		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	@Override
	public void hide() {
		accountWidgetModal.hide();
	}
	
	@Override
	public void show(BusinessAccountDTO account) {
		this.account = account;
		setupHandlers();
		displayAccount(account);
		accountWidgetModal.show();
	}
	
	public void displayAccount(BusinessAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		name.setInnerHTML(account.getName());
		profileImageUrl.setUrl(account.getImageUrl());
	}

//	public void setPresenter(HomePresenter2 presenter) {
//		this.presenter = presenter;
//	}

	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}
}
