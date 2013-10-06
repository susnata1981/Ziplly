package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.AbstractAccountView;

public class AccountDetailsWidget extends AbstractAccountView {
	private static AccountDetailsWidgetUiBinder uiBinder = GWT
			.create(AccountDetailsWidgetUiBinder.class);

	interface AccountDetailsWidgetUiBinder extends
			UiBinder<Widget, AccountDetailsWidget> {
	}

	@UiField
	NavLink aboutLink;
	
	@UiField
	NavLink interestLink;
	
	@UiField
	NavLink familyLink;
	
	@UiField
	HTMLPanel profilePanel;
	
	@UiField
	HTMLPanel aboutPanel;
	
	@UiField
	HTMLPanel interestPanel;
	
	@UiField
	HTMLPanel familyPanel;
	
	public AccountDetailsWidget(SimpleEventBus eventBus) {
		super(eventBus);
	}
	
	@Override
	protected void internalOnUserLogin() {
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		hideAllPanel();
		setVisible(aboutPanel);
		setupEventHandlers();
	}

	void setupEventHandlers() {
		aboutLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				aboutLink.setActive(false);
				setVisible(aboutPanel);
			}
		});
		interestLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				interestLink.setDisabled(true);
				setVisible(interestPanel);
			}
		});
		familyLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				disableLinks();
				setVisible(familyPanel);
			}
		});
	}

	void hideAllPanel() {
		aboutPanel.setVisible(false);
		interestPanel.setVisible(false);
		familyPanel.setVisible(false);
	}
	
	void disableLinks() {
		aboutLink.getElement().getStyle().setBackgroundColor("#0000");
		interestLink.setDisabled(true);
		familyLink.setDisabled(true);
	}
	
	private void setVisible(HTMLPanel panel) {
		hideAllPanel();
		panel.setVisible(true);
	}
	
	@Override
	protected void setupUiElements() {
	}

}
