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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.activities.PersonalAccountSettingsActivity.IPersonalAccountSettingsView;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.client.widget.ShareSettingsWidget;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.Activity;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.ValidationResult;

public class PersonalAccountSettingsView extends Composite implements IPersonalAccountSettingsView {

	private static PersonalAccountSettingsViewUiBinder uiBinder = GWT
			.create(PersonalAccountSettingsViewUiBinder.class);

	interface PersonalAccountSettingsViewUiBinder extends
			UiBinder<Widget, PersonalAccountSettingsView> {
	}

	// Aside section
	@UiField
	Anchor profileLink;
	Anchor inboxLink;

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
	ShareSettingsWidget emailShareSetting;

	@UiField
	ShareSettingsWidget occupationShareSetting;

	// Notification settings;
	@UiField
	ListBox personalMessageSettingsListBox;
	@UiField
	ListBox securitySettingsListBox;

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
	Tab passwordTab;

	@UiField
	FormPanel uploadForm;
	@UiField
	Button uploadBtn;
	@UiField
	Image profileImagePreview;

	@UiField
	HTMLPanel buttonsPanel;

	// Reset Password tab
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

	Map<Activity, CheckBox> interestToCheckboxMap = new HashMap<Activity, CheckBox>();

	private PersonalAccountDTO account;
	private AccountSettingsPresenter<PersonalAccountDTO> presenter;

	public PersonalAccountSettingsView() {
		initWidget(uiBinder.createAndBindUi(this));

		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);

		for (Activity activity : Activity.values()) {
			CheckBox cb = new CheckBox(activity.name().toLowerCase());
			interestToCheckboxMap.put(activity, cb);
			interestTabPanel.add(cb);
		}

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
	}

	void showButtonsPanel(boolean show) {
		buttonsPanel.setVisible(show);
	}

	void populateFields(PersonalAccountDTO account) {
		if (account == null) {
			return;
		}

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
		for (InterestDTO interest : account.getInterests()) {
			Activity activity = Activity.valueOf(interest.getName().toUpperCase());
			interestToCheckboxMap.get(activity).setValue(true);
		}

		// location
		zip.setText(Integer.toString(account.getZip()));

		// privacy settings
		populatePrivacySettings(account);

		// notification settings
		popoulateNotificationSettings(account);
	}

	private void populatePrivacySettings(PersonalAccountDTO account) {
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			switch (ps.getSection()) {
			case EMAIL:
				emailShareSetting.setSelection(ps.getSetting());
				break;
			case OCCUPATION:
				occupationShareSetting.setSelection(ps.getSetting());
			default:
			}
		}
	}

	private void popoulateNotificationSettings(PersonalAccountDTO account) {
		securitySettingsListBox.clear();
		for (NotificationAction action : NotificationAction.values()) {
			securitySettingsListBox.addItem(action.getName());
		}

		personalMessageSettingsListBox.clear();
		for (NotificationAction action : NotificationAction.values()) {
			personalMessageSettingsListBox.addItem(action.getName());
		}

		for (AccountNotificationSettingsDTO ans : account.getNotificationSettings()) {
			if (ans.getType() == NotificationType.PERSONAL_MESSAGE) {
				personalMessageSettingsListBox.setSelectedIndex(ans.getAction().ordinal());
			} else if (ans.getType() == NotificationType.SECURITY_ALERT) {
				securitySettingsListBox.setSelectedIndex(ans.getAction().ordinal());
			}
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
		for (Entry<Activity, CheckBox> entry : interestToCheckboxMap.entrySet()) {
			CheckBox cb = entry.getValue();
			if (cb.getValue()) {
				InterestDTO i = new InterestDTO();
				Activity a = Activity.valueOf(cb.getText().toUpperCase());
				i.setInterestId(new Long(a.ordinal() + 1));
				i.setName(entry.getKey().name().toLowerCase());
				selectedInterests.add(i);
			}
		}
		account.getInterests().clear();
		account.getInterests().addAll(selectedInterests);

		// get privacy settings
		ShareSetting emailShareSettings = emailShareSetting.getSelection();
		ShareSetting occupationShareSettings = occupationShareSetting.getSelection();
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			switch (ps.getSection()) {
			case EMAIL:
				ps.setSetting(emailShareSettings);
				break;
			case OCCUPATION:
				ps.setSetting(occupationShareSettings);
				break;
			default:
			}
		}

		// get notification settings
		NotificationAction securityAlertAction = NotificationAction.values()[securitySettingsListBox
				.getSelectedIndex()];
		NotificationAction personalMessageAlertAction = NotificationAction.values()[personalMessageSettingsListBox
				.getSelectedIndex()];

		for (AccountNotificationSettingsDTO ans : account.getNotificationSettings()) {
			ans.setAccount(account);
			if (ans.getType() == NotificationType.SECURITY_ALERT) {
				ans.setAction(securityAlertAction);
			} else {
				ans.setAction(personalMessageAlertAction);
			}
		}

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
		uploadBtn.setEnabled(true);
	}

	@Override
	public void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler submitCompleteHandler) {
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

	@UiHandler("profileLink")
	void profileLinkClicked(ClickEvent event) {
		presenter.onProfileLinkClick();
	}

	@UiHandler("inboxLink")
	void messagesLinkClicked(ClickEvent event) {
		presenter.onInboxLinkClick();
	}
}