package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.view.AbstractAccountView;

public class EditAccountDetailsWidget extends AbstractAccountView {

	private static EditAccountDetailsWidgetUiBinder uiBinder = GWT
			.create(EditAccountDetailsWidgetUiBinder.class);

	interface EditAccountDetailsWidgetUiBinder extends
			UiBinder<Widget, EditAccountDetailsWidget> {
	}

	@UiField
	Modal accountDetailsModal;
	
	@UiField
	TabPanel accountDetailsTab;
	
	@UiField
	Element firstname;
	
	@UiField
	Element lastname;
	
	@UiField
	Element email;
	
	@UiField
	TextBox zip;
	
	@UiField
	Alert message;
	
	@UiField
	Button saveBtn;
	
	@UiField
	Button closeBtn;
	
	@Inject
	public EditAccountDetailsWidget(CachingDispatcherAsync dispatcher, SimpleEventBus eventBus) {
		super(dispatcher, eventBus);
	}

	@Override
	protected void internalOnUserLogin() {
		populateFields();
	}

	private void populateFields() {
		// basic info
		firstname.setInnerText(account.getFirstName());		
		lastname.setInnerText(account.getLastName());
		email.setInnerText(account.getEmail());
		
		// location
		zip.setText(Integer.toString(account.getZip()));
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
	}

	@Override
	protected void setupUiElements() {
	}

	@UiHandler("closeBtn")
	void close(ClickEvent event) {
		hide();
	}
	
	@UiHandler("saveBtn")
	void save(ClickEvent event) {
		displayError();
		if (!validate()) {
			displayError();
			return;
		}
		
		// TODO 
		// call service
	}
	
	private void clearError() {
		message.setVisible(false);
		message.clear();
	}
	
	private void displayError() {
		message.setType(AlertType.ERROR);
		message.setText("Erorrs exists in the form");
		message.setVisible(true);
	}
	
	boolean validate() {
		return false;
	}

	public void show(AccountDetailsType adt) {
		clearError();
		accountDetailsTab.selectTab(adt.ordinal());
		accountDetailsModal.show();
	}
	
	public void hide() {
		accountDetailsModal.hide();
	}
}
