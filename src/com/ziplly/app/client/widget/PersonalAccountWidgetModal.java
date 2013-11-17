package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.HomeView.HomePresenter;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountWidgetModal extends Composite implements IAccountWidgetModal<PersonalAccountDTO> {

	private static AccountWidgetModalUiBinder uiBinder = GWT
			.create(AccountWidgetModalUiBinder.class);

	interface AccountWidgetModalUiBinder extends
			UiBinder<Widget, PersonalAccountWidgetModal> {
	}

	@UiField
	Modal accountWidgetModal;
	
	@UiField
	Image profileImageUrl;

	@UiField
	Element name;

	@UiField
	Element introduction;

	@UiField
	HTMLPanel interestListPanel;

	@UiField
	Element occupation;

	@UiField
	Button viewProfileBtn;
	
	@UiField
	Button cancelBtn;

	private HomePresenter presenter;

	private PersonalAccountDTO account;
	
	public PersonalAccountWidgetModal() {
		initWidget(uiBinder.createAndBindUi(this));
		hide();
		setupHandlers();
	}

	private void setupHandlers() {
		viewProfileBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getPresenter().goTo(new PersonalAccountPlace(account.getAccountId()));
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
	public void show(PersonalAccountDTO account) {
		this.account = account;
		displayAccount(account);
		accountWidgetModal.show();
	}
	
	public void displayAccount(PersonalAccountDTO account) {
		if (account == null) {
			throw new IllegalArgumentException();
		}
		
		profileImageUrl.setUrl(account.getImageUrl());
		name.setInnerHTML(account.getDisplayName());
		if (account.getIntroduction() != null) {
			introduction.setInnerText(account.getIntroduction());
		}
		occupation.setInnerText(account.getOccupation());
		populateInterests(account);
	}

	void populateInterests(PersonalAccountDTO account) {
		interestListPanel.clear();
		for (InterestDTO interest : account.getInterests()) {
			interestListPanel.add(new Label(interest.getName()));
		}
	}
	public PersonalAccountWidgetModal(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HomePresenter getPresenter() {
		return presenter;
	}

	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}
}
