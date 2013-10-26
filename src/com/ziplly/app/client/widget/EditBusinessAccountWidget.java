package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class EditBusinessAccountWidget extends Composite implements EditAccount<BusinessAccountDTO> {

	private static EditBusinessAccountWidgetUiBinder uiBinder = GWT
			.create(EditBusinessAccountWidgetUiBinder.class);

	interface EditBusinessAccountWidgetUiBinder extends
			UiBinder<Widget, EditBusinessAccountWidget> {
	}

	@UiField
	Modal businessAccountDetailsModal;
	
	@UiField
	Alert message;
	
	@UiField
	ControlGroup nameCg;
	HelpInline nameError;
	@UiField
	TextBox nameTextBox;
	
	@UiField
	TextBox websiteTextBox;
	
	@UiField
	TextBox emailTextBox;
	
	@UiField
	TextBox phoneTextBox;
	
	@UiField
	TextBox zipTextBox;
	
	@UiField
	Button saveBtn;
	
	@UiField
	Button closeBtn;

	private AccountPresenter<BusinessAccountDTO> presenter;

	private BusinessAccountDTO account;
	
	public EditBusinessAccountWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		businessAccountDetailsModal.setAnimation(true);
	}

	@Override
	public void display(BusinessAccountDTO account) {
		if (account != null) {
			this.account = account;
			message.clear();
			message.setVisible(false);
			nameTextBox.setText(account.getName());
			websiteTextBox.setText(account.getWebsite());
			emailTextBox.setText(account.getEmail());
			phoneTextBox.setText(account.getPhone());
			zipTextBox.setText(Integer.toString(account.getZip()));
			businessAccountDetailsModal.show();
		}
	}

	@Override
	public void show() {
		businessAccountDetailsModal.show();
	}
	
	@Override
	public void hide() {
		businessAccountDetailsModal.hide();
	}
	
	// TODO (complete list)
	@Override
	public boolean validate() {
		boolean valid = true;
		ValidationResult result = FieldVerifier.validateName(nameTextBox.getText());
		if (!result.isValid()) {
			nameCg.setType(ControlGroupType.ERROR);
			nameError.setText(result.getErrors().get(0).getErrorMessage());
			valid = false;
		}
		return valid;
	}
	
	@Override
	@UiHandler("saveBtn")
	public void save(ClickEvent event) {
		BusinessAccountDTO request = new BusinessAccountDTO();
		request.setAccountId(account.getAccountId());
		request.setUid(account.getUid());
		request.setName(FieldVerifier.sanitize(nameTextBox.getText()));
		request.setWebsite(FieldVerifier.sanitize(websiteTextBox.getText()));
		request.setEmail(FieldVerifier.sanitize(emailTextBox.getText()));
		request.setPhone(FieldVerifier.sanitize(phoneTextBox.getText()));
		request.setZip(Integer.parseInt(FieldVerifier.sanitize(zipTextBox.getText())));
		
//		account.setName(FieldVerifier.sanitize(nameTextBox.getText()));
//		account.setWebsite(FieldVerifier.sanitize(websiteTextBox.getText()));
//		account.setEmail(FieldVerifier.sanitize(emailTextBox.getText()));
//		account.setPhone(FieldVerifier.sanitize(phoneTextBox.getText()));
//		account.setZip(Integer.parseInt(FieldVerifier.sanitize(zipTextBox.getText())));

		getPresenter().save(request);
	}

	@Override
	@UiHandler("closeBtn")
	public void cancel(ClickEvent event) {
		businessAccountDetailsModal.hide();
	}
	
	@Override
	public void clearError() {
		message.setVisible(false);
		message.clear();
	}
	
	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}

	public AccountPresenter<BusinessAccountDTO> getPresenter() {
		return presenter;
	}

	public void setPresenter(AccountPresenter<BusinessAccountDTO> presenter) {
		this.presenter = presenter;
	}
}
