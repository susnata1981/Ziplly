package com.ziplly.app.client.view.account;

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
import com.github.gwtbootstrap.client.ui.Label;
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
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.HPanel;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.client.widget.ShareSettingsWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.model.RecordStatus;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.ValidationResult;

public class PersonalAccountSettingsView extends AbstractView implements IPersonalAccountSettingsView {

	private static final int MAX_INTRODUCTION_LENGTH = 255;

	private static final int MAX_OCCUPATION_LENGTH = 50;

	private static PersonalAccountSettingsViewUiBinder uiBinder = GWT
	    .create(PersonalAccountSettingsViewUiBinder.class);

	interface Style extends CssResource {
	  String profileInfoHeaderNoBackground();
	  
	  String profileInfoInlineValue();
	}
	
	interface PersonalAccountSettingsViewUiBinder extends
	    UiBinder<Widget, PersonalAccountSettingsView> {
	}

	@UiField
	Style style;
	
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
	ListBox genderList;

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
	Image uploadAnchorIcon;
	@UiField
	HTMLPanel profileImagePanel;
	@UiField
	Image profileImagePreview;
	@UiField
	Image loadingImage;

	// State variables - create abstraction.
	private boolean imageUploaded;
	private ImageDTO currentUploadedImage;

	//
	// Account Notification settings
	//
	@UiField
	HTMLPanel notificationPanel;
	private Map<AccountNotificationSettingsDTO, ListBox> accountNotificationSettingsMap =
	    new HashMap<AccountNotificationSettingsDTO, ListBox>();

	//
	// Privacy settings
	//
	@UiField
	HTMLPanel privacyPanel;
	private Map<PrivacySettingsDTO, ShareSettingsWidget> privacySettingsMap =
	    new HashMap<PrivacySettingsDTO, ShareSettingsWidget>();
//	private PrivacySettingsFormatter privacySettingsFormatter =
//	    (PrivacySettingsFormatter) AbstractValueFormatterFactory
//	        .getValueFamilyFormatter(ValueFamilyType.PRIVACY_SETTINGS);

	//
	// Occupation
	//
	@UiField
	Tab occupationTab;
	@UiField
	ControlGroup occupationCg;
	@UiField
	TextBox occupation;
	@UiField
	HelpInline occupationError;

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
	private PersonalAccountSettingsPresenter presenter;

	@Inject
	public PersonalAccountSettingsView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		StyleHelper.show(loadingImage.getElement(), false);
		message.setVisible(false);

		// Setup upload anchor
		uploadAnchorIcon.setUrl(ZResources.IMPL.uploadIcon().getSafeUri());
		StyleHelper.show(uploadField.getElement(), false);
		StyleHelper.show(introductionError.getElement(), false);
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

		uploadAnchorIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				uploadField.getElement().<InputElement> cast().click();
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
	 * 
	 * @param account
	 */
	void populateFields(PersonalAccountDTO account) {
		if (account == null) {
			return;
		}

		this.account = account;

		// basic info
		setProfileImage(accountFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
//		adjustProfileImagePanel();

		firstname.setInnerText(account.getFirstName());
		lastname.setInnerText(account.getLastName());
		email.setInnerText(account.getEmail());
		introduction.setText(account.getIntroduction());

		// gender
		for (Gender g : Gender.getValuesForSignup()) {
			genderList.addItem(basicDataFormatter.format(g, ValueType.GENDER));
		}
		genderList.setSelectedValue(basicDataFormatter.format(account.getGender(), ValueType.GENDER));

		// occupation
		occupation.setText(account.getOccupation());

		// location
		neighborhoodSpan.setInnerHTML(basicDataFormatter.format(account
		    .getLocations()
		    .get(0)
		    .getNeighborhood(), ValueType.NEIGHBORHOOD));

		// privacy settings
		populatePrivacySettings(account);

		// notification settings
		popoulateNotificationSettings(account);
	}

	/**
	 * Populates privacy settings
	 */
	private void populatePrivacySettings(PersonalAccountDTO account) {
		privacyPanel.clear();
		sortPrivacySettings(account.getPrivacySettings());

		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			HPanel panel = new HPanel();
			Label privacySettingsLabel = new Label(ps.getSection().getName());
			privacySettingsLabel.setStyleName(style.profileInfoHeaderNoBackground());
			
			FlowPanel span = new FlowPanel();//privacySettingsFormatter.format(ps, ValueType.PRIVACY_FIELD_NAME));
			span.add(privacySettingsLabel);
			span.setStyleName(style.profileInfoHeaderNoBackground());
			ShareSettingsWidget shareSettingWidget = new ShareSettingsWidget(ps.getAllowedShareSettings());
			shareSettingWidget.setSelection(ps.getSetting());
			span.setWidth("120px");
			panel.add(span);
			panel.add(shareSettingWidget);
			// notificationPanel.add(panel);
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
	 * 
	 * @param account
	 */
	private void popoulateNotificationSettings(PersonalAccountDTO account) {
		accountNotificationSettingsMap.clear();
		notificationPanel.clear();
		Collections.sort(
		    account.getNotificationSettings(),
		    new Comparator<AccountNotificationSettingsDTO>() {

			    @Override
			    public int compare(AccountNotificationSettingsDTO o1, AccountNotificationSettingsDTO o2) {
				    return o1.getType().name().compareTo(o2.getType().name());
			    }
		    });

		for (AccountNotificationSettingsDTO ans : account.getNotificationSettings()) {
			ListBox action = getNotificationActionListBox();
			action.setSelectedIndex(ans.getAction().ordinal());
			HPanel panel = new HPanel();
			FlowPanel span = new FlowPanel();
	    Label notificationLabel = new Label(ans.getType().getNotificationName());
	    notificationLabel.setStyleName(style.profileInfoHeaderNoBackground());
	    span.add(notificationLabel);
			
//			HTMLPanel span =
//			    new HTMLPanel(basicDataFormatter.format(ans.getType(), ValueType.NOTIFICATION_TYPE));
//			span.setWidth("120px");
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
//		introductionError.setVisible(false);
		StyleHelper.show(introductionError.getElement(), false);
		occupationCg.setType(ControlGroupType.NONE);
		occupationError.setVisible(false);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
	}

	// TODO
	boolean validate() {
		boolean valid =
		    validateText(introduction, introductionCg, introductionError, MAX_INTRODUCTION_LENGTH);
		valid &= validateText(occupation, occupationCg, occupationError, MAX_OCCUPATION_LENGTH);

		return valid;
	}

	private boolean validateText(HasText elem, ControlGroup cg, HelpInline error, int maxLength) {
		// No need to validate if it's empty
		if (elem.getText().length() == 0) {
			return true;
		}

		ValidationResult result = FieldVerifier.validateString(elem.getText(), maxLength);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			StyleHelper.show(introductionError.getElement(), true);
			error.setText(result.getErrors().get(0).getErrorMessage());
			message.setText(result.getErrors().get(0).getErrorMessage());
			message.setVisible(true);
			return false;
		}

		return true;
	}

	@Override
	public void clear() {
		clearError();
		StyleHelper.clearBackground();
	}

	@Override
	public void setPresenter(PersonalAccountSettingsPresenter presenter) {
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

		// Image
		if (imageUploaded) {
			// TODO: change it to take multiple images.
			for(ImageDTO imageDto : account.getImages()) {
				imageDto.setStatus(RecordStatus.DELETED);
			}
			
			account.getImages().add(currentUploadedImage);
		}

		// TODO validation
		if (!introduction.getText().equals("")) {
			account.setIntroduction(introduction.getText());
		}

		if (!occupation.getText().equals("")) {
			account.setOccupation(occupation.getText());
		}

		// Gender
		account.setGender(Gender.getValuesForSignup().get(genderList.getSelectedIndex()));

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
		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
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
		showSaveButton(false);
	}

	@Override
	public void displayImagePreview(String imageUrl) {
		if (imageUrl != null) {
			currentUploadedImage = ImageUtil.parseImageUrl(imageUrl);
			setProfileImage(currentUploadedImage.getUrl());
			StyleHelper.show(loadingImage.getElement(), false);
		}
	}

	/**
	 * Sets the profile image
	 */
	private void setProfileImage(final String imageUrl) {
		if (imageUrl != null) {
//			resetProfileImagePanel();
			profileImagePreview.setUrl(imageUrl);
//			adjustProfileImagePanel();
		}
	}

	/**
	 * Adjusts the height of profile image panel.
	 */
	private void adjustProfileImagePanel() {
		profileImagePreview.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
			  Window.alert("IMG HEIGHT="+profileImagePreview.getHeight());
				StyleHelper.setHeight(profileImagePanel, profileImagePreview.getHeight());
			}
		});
	}

	private void resetProfileImagePanel() {
		profileImagePanel.setHeight("200px");
	}

	@Override
	public void resetUploadForm() {
		uploadForm.setAction("");
	}

	@Override
	public void onUpload() {
		uploadForm.submit();
		StyleHelper.show(loadingImage.getElement(), true);
		loadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri().asString());
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
		valid &= validatePassword(confirmPasswordInput, confirmNewPasswordCg, confirmNewPasswordError);

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
		for (InterestDTO interest : account.getInterests()) {
			interestToCheckboxMap.get(interest).setValue(true);
		}
	}

	@Override
	public void showSaveButton(boolean show) {
		saveBtn.setEnabled(show);
	}

}