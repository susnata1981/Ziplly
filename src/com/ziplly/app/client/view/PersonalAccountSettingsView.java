package com.ziplly.app.client.view;

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
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSettingsWidget;
import com.ziplly.app.model.AccountSettingDTO;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class PersonalAccountSettingsView extends Composite implements ISettingsView<PersonalAccountDTO>{

	private static PersonalAccountSettingsViewUiBinder uiBinder = GWT
			.create(PersonalAccountSettingsViewUiBinder.class);

	interface PersonalAccountSettingsViewUiBinder extends
			UiBinder<Widget, PersonalAccountSettingsView> {
	}

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
	TextArea introduction;
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
	Button cancelBtn;
	
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
	
	@UiField
	FormPanel uploadForm;
	@UiField
	Button uploadBtn;
	@UiField
	Image profileImagePreview;
	
	Map<AccountDetailsType, Tab> accountDetailsTypeToTabsMap = new HashMap<AccountDetailsType, Tab>();
	Map<Tab, ShareSettingsWidget> tabsToShareSettingWidgetMap = new HashMap<Tab, ShareSettingsWidget>();

	Map<Activity, CheckBox> interestToCheckboxMap = new HashMap<Activity, CheckBox>();
	
	private PersonalAccountDTO account;
	private AccountSettingsPresenter<PersonalAccountDTO> presenter;
	
	public PersonalAccountSettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		
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
		
		message.setVisible(false);
	}

	void populateFields(PersonalAccountDTO account) {
		// basic info
		if (account.getImageUrl() != null) {
			displayImagePreview(account.getImageUrl());
		}
		firstname.setInnerText(account.getFirstName());		
		lastname.setInnerText(account.getLastName());
		email.setInnerText(account.getEmail());
		introduction.setText(account.getIntroduction());

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

	// TODO apply SafeHtmlUtils
	@UiHandler("saveBtn")
	void save(ClickEvent event) {
		onSave();
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
	
	// TODO
	boolean validate() {
		return true;
	}

	@Override
	public void clear() {
		for(CheckBox cb: interestToCheckboxMap.values()) {
			cb.setEnabled(false);
		}
		clearError();
	}

	@Override
	public void setPresenter(
			AccountSettingsPresenter<PersonalAccountDTO> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void displaySettings(PersonalAccountDTO account) {
		this.account = account;
		resetUploadForm();
		populateFields(account);
	}

	// TODO validation required!
	@Override
	public void onSave() {
		if (!validate()) {
			displayMessage(StringConstants.FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
			return;
		}
		
		String imageUrl = profileImagePreview.getUrl();
		if (imageUrl != null) {
			account.setImageUrl(imageUrl);
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
		
		// TODO 
		// call presenter
		presenter.save(account);
	}

	@Override
	public void displayImagePreview(String imageUrl) {
		profileImagePreview.setUrl(imageUrl);
	}

	@Override
	public void resetUploadForm() {
		uploadBtn.setEnabled(false);
		uploadForm.setAction("");
	}
	
	@Override
	public void onUpload() {
		uploadForm.submit();
	}
	
	@UiHandler("uploadBtn")
	void uploadImage(ClickEvent event) {
		onUpload();
	}

	@Override
	public void setUploadFormActionUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	@Override
	public void setUploadFormSubmitCompleteHandler(
			SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void onCancel() {
		presenter.cancel();
	}

	@UiHandler("cancelBtn")
	void cancel(ClickEvent event) {
		onCancel();
	}
}