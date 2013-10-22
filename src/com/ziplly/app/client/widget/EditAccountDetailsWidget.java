package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountActivityPresenter;
import com.ziplly.app.client.view.View;
import com.ziplly.app.model.AccountSettingDTO;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

/*
 * The tabs in this view are ordered as per the AccountDetailsType enum
 */
public class EditAccountDetailsWidget extends Composite implements View<AccountActivityPresenter> {

	private static final String ACCOUNT_SAVE_SUCCESSFUL = "Account updated";
	private static final String FAILED_TO_SAVE_ACCOUNT = "Failed to save account";
	
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
	ControlGroup introductionCg;
	@UiField
	TextBox introduction;
	@UiField
	HelpInline introductionError;
	
	@UiField
	TextBox zip;
	
	@UiField
	TextBox occupation;
	
	@UiField
	Alert message;
	
	@UiField
	Button saveBtn;
	
	@UiField
	Button closeBtn;

	@UiField
	Tab basicInfoTab;
	@UiField
	ShareSettingsWidget basicInfoSetting;
	
	@UiField
	Tab occupationTab;
	@UiField
	ShareSettingsWidget occupationSetting;
	
	@UiField
	Tab interestTab;
	@UiField
	ShareSettingsWidget interestSetting;
	@UiField
	HTMLPanel interestTabPanel;
	
	@UiField
	Tab locationTab;
	@UiField
	ShareSettingsWidget locationSetting;
	
	Map<AccountDetailsType, Tab> accountDetailsTypeToTabsMap = new HashMap<AccountDetailsType, Tab>();
	Map<Tab, ShareSettingsWidget> tabsToShareSettingWidgetMap = new HashMap<Tab, ShareSettingsWidget>();

	Map<Activity, CheckBox> interestToCheckboxMap = new HashMap<Activity, CheckBox>();
	
	AccountActivityPresenter presenter;
	private PersonalAccountDTO account;
	
	public EditAccountDetailsWidget() {

		initWidget(uiBinder.createAndBindUi(this));
		
		accountDetailsTypeToTabsMap.put(AccountDetailsType.BASICINFO, basicInfoTab);
		tabsToShareSettingWidgetMap.put(basicInfoTab, basicInfoSetting);
		
		accountDetailsTypeToTabsMap.put(AccountDetailsType.OCCUPATION, occupationTab);
		tabsToShareSettingWidgetMap.put(occupationTab, occupationSetting);
		
		accountDetailsTypeToTabsMap.put(AccountDetailsType.INTEREST, interestTab);
		tabsToShareSettingWidgetMap.put(interestTab, interestSetting);
		
		accountDetailsTypeToTabsMap.put(AccountDetailsType.LOCATION, locationTab);
		tabsToShareSettingWidgetMap.put(locationTab, locationSetting);

		for(Activity activity : Activity.values()) {
			CheckBox cb = new CheckBox(activity.name().toLowerCase());
			interestToCheckboxMap.put(activity, cb);
			interestTabPanel.add(cb);
		}
	}

	void populateFields(PersonalAccountDTO account) {
		// basic info
		firstname.setInnerText(account.getFirstName());		
		lastname.setInnerText(account.getLastName());
		email.setInnerText(account.getEmail());

		// occupation
		occupation.setText(account.getOccupation());
		
		// interests
		for(InterestDTO interest : account.getInterests()) {
			Activity activity = Activity.valueOf(interest.getName().toUpperCase());
			interestToCheckboxMap.get(activity).setValue(true);
		}
		
		// location
		zip.setText(Integer.toString(account.getZip()));
		
		// account settings
		for(AccountSettingDTO asd: account.getAccountSettings()) {
			Tab tab = accountDetailsTypeToTabsMap.get(asd.getSection());
			tabsToShareSettingWidgetMap.get(tab).setSelection(asd.getSetting());
		}
	}

	@UiHandler("closeBtn")
	void close(ClickEvent event) {
		hide();
	}
	
	// TODO apply SafeHtmlUtils
	@UiHandler("saveBtn")
	void save(ClickEvent event) {
		if (!validate()) {
			displayMessage(FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
			return;
		}
		
		// TODO validation
		if (!introduction.getText().equals("")) {
			account.setIntroduction(introduction.getText());
		}
		
		if (!zip.getText().equals("")) {
			account.setZip(Integer.parseInt(zip.getText()));
		}
		
		if (!occupation.getText().equals("")) {
			account.setOccupation(occupation.getText());
		}
		// interests
		List<InterestDTO> selectedInterests = new ArrayList<InterestDTO>();
		for(Entry<Activity, CheckBox> entry : interestToCheckboxMap.entrySet()) {
			CheckBox cb = entry.getValue();
			if (cb.getValue()) {
				InterestDTO i = new InterestDTO();
				i.setName(entry.getKey().name().toLowerCase());
				selectedInterests.add(i);
			}
		}
		account.getInterests().clear();
		account.getInterests().addAll(selectedInterests);
		
//		account.setCity(city);
		// TODO 
		// call presenter
		presenter.save(account);
	}
	
	public void clearError() {
		message.setVisible(false);
		message.clear();
	}
	
	public void displaySuccessMessage() {
		displayMessage(ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
	}
	
	public void displayErrorMessage() {
		displayMessage(FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
	}
	
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}
	
	// TODO
	boolean validate() {
		return true;
	}

	public void show(AccountDetailsType adt) {
		clearError();
		accountDetailsTab.selectTab(adt.ordinal());
		accountDetailsModal.show();
	}
	
	public void hide() {
		accountDetailsModal.hide();
	}

	@Override
	public void clear() {
		for(CheckBox cb: interestToCheckboxMap.values()) {
			cb.setEnabled(false);
		}
	}

	@Override
	public void setPresenter(AccountActivityPresenter presenter) {
		this.presenter = presenter;
	}

	public void displayAccount(PersonalAccountDTO account) {
		this.account = account;
		populateFields(account);
	}
}
