package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent.Handler;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.activities.PersonalAccountSettingsActivity.IPersonalAccountSettingsView;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AccountNotificationSettingsFormatter;
import com.ziplly.app.client.view.factory.PrivacySettingsFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.HPanel;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.client.widget.ShareSettingsWidget;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.ValidationResult;

public class PersonalAccountSettingsView extends AbstractView implements IPersonalAccountSettingsView{

	private static final int MAX_INTRODUCTION_LENGTH = 255;

	private static PersonalAccountSettingsViewUiBinder uiBinder = GWT
			.create(PersonalAccountSettingsViewUiBinder.class);

	interface PersonalAccountSettingsViewUiBinder extends
			UiBinder<Widget, PersonalAccountSettingsView> {
	}

	@UiField
	TabPanel accountDetailsTab;

	//
	// Basic account info 
	//
	@UiField
	Tab basicInfoTab;
	
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

	// image upload section
	@UiField
	FormPanel uploadForm;
	@UiField
	FileUpload uploadField;
	@UiField
	Image profileImagePreview;
	private boolean imageUploaded;

	//
	// Account Notification settings
	//
	@UiField
	HTMLPanel notificationPanel;
	private Map<AccountNotificationSettingsDTO, ListBox> accountNotificationSettingsMap 
	  = new HashMap<AccountNotificationSettingsDTO, ListBox>();
	private AccountNotificationSettingsFormatter accountNotificationFormatter = 
			(AccountNotificationSettingsFormatter) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.ACCOUNT_NOTIFICATION_SETTINGS);

	//
	// Privacy settings
	//
	@UiField
	HTMLPanel privacyPanel;
	private Map<PrivacySettingsDTO, ShareSettingsWidget> privacySettingsMap = 
			new HashMap<PrivacySettingsDTO, ShareSettingsWidget>();
	private PrivacySettingsFormatter privacySettingsFormatter = 
			(PrivacySettingsFormatter) AbstractValueFormatterFactory.getValueFamilyFormatter(ValueFamilyType.PRIVACY_SETTINGS);

	//
	// Occupation
	//
	@UiField
	Tab occupationTab;
	@UiField
	TextBox occupation;
	
	//
	// Interest
	//
	@UiField
	Tab interestTab;
	@UiField
	HTMLPanel interestTabPanel;
	Map<InterestDTO, CheckBox> interestToCheckboxMap = new HashMap<InterestDTO, CheckBox>();
	
	//
	// Location TODO:(needs to go)
	//
	@UiField
	Tab locationTab;
	@UiField
	SpanElement neighborhoodSpan;
	@UiField
	TextBox zip;

	@UiField
	HTMLPanel buttonsPanel;

	//
	// Reset Password tab
	//
	@UiField
	Tab passwordTab;
	@UiField
	PasswordTextBox password;
	@UiField
	ControlGroup passwordCg;
	@UiField
	HelpInline passwordError;

	@UiField
	PasswordTextBox newPassword;
	@UiField
	ControlGroup newPasswordCg;
	@UiField
	HelpInline newPasswordError;

	@UiField
	PasswordTextBox confirmNewPassword;
	@UiField
	ControlGroup confirmNewPasswordCg;
	@UiField
	HelpInline confirmNewPasswordError;

	@UiField
	Button updatePasswordBtn;

	//
	// Alert Message
	//
	@UiField
	Alert message;
	
	//
	// Buttons
	//
	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;

	private PersonalAccountDTO account;
	private AccountSettingsPresenter<PersonalAccountDTO> presenter;

	@Inject
	public PersonalAccountSettingsView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

//		for (Activity activity : Activity.values()) {
//			CheckBox cb = new CheckBox(activity.getActivityName());
//			interestToCheckboxMap.put(activity, cb);
//			interestTabPanel.add(cb);
//		}

		message.setVisible(false);
		setupHandlers();
	}

	private void setupHandlers() {
		passwordTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showButtonsPanel(false);
			}
		});

		accountDetailsTab.addShowHandler(new Handler() {
			@Override
			public void onShow(ShowEvent showEvent) {
				// sort of hacky
				String tab = showEvent.getRelativeElement().getInnerHTML();
				if (tab.contains("Password")) {
					showButtonsPanel(true);
				}
			}
		});
		
		uploadField.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onUpload();
			}
		});
	}

	void showButtonsPanel(boolean show) {
		buttonsPanel.setVisible(show);
	}

	/**
	 * Main function responsible for populating the view
	 * @param account
	 */
	void populateFields(PersonalAccountDTO account) {
		if (account == null) {
			return;
		}

		this.account = account;
		
		// basic info
		displayImagePreview(accountFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
		
		firstname.setInnerText(account.getFirstName());
		lastname.setInnerText(account.getLastName());
		email.setInnerText(account.getEmail());
		introduction.setText(account.getIntroduction());

		// occupation
		occupation.setText(account.getOccupation());

		// location
		neighborhoodSpan.setInnerHTML(account.getNeighborhood().getName());
		zip.setReadOnly(true);
		zip.setText(Integer.toString(account.getZip()));

		// privacy settings
		populatePrivacySettings(account);

		// notification settings
		popoulateNotificationSettings(account);
	}

	/**
	 * Populates privacy settings
	 * @param account
	 */
	private void populatePrivacySettings(PersonalAccountDTO account) {
		privacyPanel.clear();
		sortPrivacySettings(account.getPrivacySettings());
		
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			HPanel panel = new HPanel();
			HTMLPanel span = new HTMLPanel(privacySettingsFormatter.format(ps, ValueType.PRIVACY_FIELD_NAME));
			ShareSettingsWidget shareSettingWidget = new ShareSettingsWidget(ps.getAllowedShareSettings());
			shareSettingWidget.setSelection(ps.getSetting());
			span.setWidth("120px");
			panel.add(span);
			panel.add(shareSettingWidget);
//			notificationPanel.add(panel);
			privacySettingsMap.put(ps, shareSettingWidget);
			privacyPanel.add(panel);
		}
	}

	private void sortPrivacySettings(List<PrivacySettingsDTO> privacySettings) {
		Collections.sort(account.getPrivacySettings(), new Comparator<PrivacySettingsDTO>() {

			@Override
			public int compare(PrivacySettingsDTO ps1, PrivacySettingsDTO ps2) {
				return ps1.getSection().name().compareTo(ps2.getSection().name());
			}
		});
	}

	private ListBox getNotificationActionListBox() {
		ListBox actionListBox = new ListBox();
		for (NotificationAction action : NotificationAction.values()) {
			actionListBox.addItem(action.getName());
		}
		return actionListBox;
	}
	
	/**
	 * Populates account notification settings
	 * @param account
	 */
	private void popoulateNotificationSettings(PersonalAccountDTO account) {
		accountNotificationSettingsMap.clear();
		notificationPanel.clear();
		Collections.sort(account.getNotificationSettings(), new Comparator<AccountNotificationSettingsDTO>() {

			@Override
			public int compare(AccountNotificationSettingsDTO o1, AccountNotificationSettingsDTO o2) {
				return o1.getType().name().compareTo(o2.getType().name());
			}
		});
		
		for (AccountNotificationSettingsDTO ans : account.getNotificationSettings()) {
			ListBox action = getNotificationActionListBox();
			action.setSelectedIndex(ans.getAction().ordinal());
			HPanel panel = new HPanel();
			HTMLPanel span = new HTMLPanel(basicDataFormatter.format(ans.getType(), ValueType.NOTIFICATION_TYPE));
			span.setWidth("120px");
			panel.add(span);
			panel.add(action);
			notificationPanel.add(panel);
			accountNotificationSettingsMap.put(ans, action);
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
		introductionCg.setType(ControlGroupType.NONE);
		introductionError.setVisible(false);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}

	// TODO
	boolean validate() {
		ValidationResult result = FieldVerifier.validateString(introduction.getText(), MAX_INTRODUCTION_LENGTH);
		if (!result.isValid()) {
			introductionCg.setType(ControlGroupType.ERROR);
			introductionError.setVisible(true);
			introductionError.setText(result.getErrors().get(0).getErrorMessage());
			return false;
		}
		
		return true;
	}

	@Override
	public void clear() {
		clearError();
	}

	@Override
	public void setPresenter(AccountSettingsPresenter<PersonalAccountDTO> presenter) {
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
		clearError();
		if (!validate()) {
			return;
		}

		if (imageUploaded) {
			account.setImageUrl(profileImagePreview.getUrl());
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
		for (Entry<InterestDTO, CheckBox> entry : interestToCheckboxMap.entrySet()) {
			CheckBox cb = entry.getValue();
			if (cb.getValue()) {
				selectedInterests.add(entry.getKey());
			}
		}
		account.getInterests().clear();
		account.getInterests().addAll(selectedInterests);
		
		//
		// Privacy Settings
		//
		for(PrivacySettingsDTO ps : account.getPrivacySettings()) {
			ShareSetting shareSettings = privacySettingsMap.get(ps).getSelection();
			ps.setSetting(shareSettings);
		}
		
		//
		// AccountNotificationSettings
		//
		for (AccountNotificationSettingsDTO ans : account.getNotificationSettings()) {
			int selectedIndex = accountNotificationSettingsMap.get(ans).getSelectedIndex();
			NotificationAction action = NotificationAction.values()[selectedIndex];
			ans.setAction(action);
		}

		// call presenter
		presenter.save(account);
	}

	@Override
	public void displayImagePreview(String imageUrl) {
		if (imageUrl != null) {
			profileImagePreview.setUrl(imageUrl);
		}
	}

	@Override
	public void resetUploadForm() {
		uploadForm.setAction("");
	}

	@Override
	public void onUpload() {
		uploadForm.submit();
		imageUploaded = true;
	}

	@UiHandler("updatePasswordBtn")
	void resetPassword(ClickEvent event) {
		resetPasswordErrors();
		if (!validatePasswordInput()) {
			return;
		}

		String oldPasswordInput = FieldVerifier.sanitize(password.getText());
		String newPasswordInput = FieldVerifier.sanitize(newPassword.getText());
		UpdatePasswordAction action = new UpdatePasswordAction();
		action.setOldPassword(oldPasswordInput);
		action.setNewPassword(newPasswordInput);
		presenter.updatePassword(action);
		clearPasswordFields();
	}

	private void clearPasswordFields() {
		password.setText("");
		newPassword.setText("");
		confirmNewPassword.setText("");
	}

	boolean validatePassword(String password, ControlGroup cg, HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validatePassword(password);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	private boolean validatePasswordInput() {
		String passwordInput = password.getText().trim();
		boolean valid = validatePassword(passwordInput, passwordCg, passwordError);

		String newPasswordInput = newPassword.getText().trim();
		valid &= validatePassword(newPasswordInput, newPasswordCg, newPasswordError);

		String confirmPasswordInput = confirmNewPassword.getText().trim();
		valid &= validatePassword(confirmPasswordInput, confirmNewPasswordCg,
				confirmNewPasswordError);

		if (newPasswordInput != null && confirmPasswordInput != null) {
			if (!confirmPasswordInput.equals(newPasswordInput)) {
				confirmNewPasswordCg.setType(ControlGroupType.ERROR);
				confirmNewPasswordError.setText(StringConstants.PASSWORD_MISMATCH_ERROR);
				confirmNewPasswordError.setVisible(true);
			}
		}
		return valid;
	}

	void resetPasswordErrors() {
		message.clear();
		message.setVisible(false);
		passwordCg.setType(ControlGroupType.NONE);
		passwordError.setVisible(false);
		newPasswordCg.setType(ControlGroupType.NONE);
		newPasswordError.setVisible(false);
		confirmNewPasswordCg.setType(ControlGroupType.NONE);
		confirmNewPasswordError.setVisible(false);
	}

	@Override
	public void setUploadFormActionUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
	}

	@Override
	public void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void onCancel() {
		presenter.cancel();
	}

	@Override
	public void enableSaveButton() {
		saveBtn.setEnabled(true);
	}
	
	@UiHandler("cancelBtn")
	void cancel(ClickEvent event) {
		onCancel();
	}

	@Override
	public void displayAllInterests(List<InterestDTO> interests) {
		interestTabPanel.clear();
		interestToCheckboxMap.clear();
		for (InterestDTO interest : interests) {
			CheckBox cb = new CheckBox(interest.getName());
			interestToCheckboxMap.put(interest, cb);
			interestTabPanel.add(cb);
		}
		markSelectedInterest();
	}
	
	private void markSelectedInterest() {
		for(InterestDTO interest : account.getInterests()) {
			interestToCheckboxMap.get(interest).setEnabled(true);
		}
	}
}