package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.client.widget.NeighborhoodSelectorWidget;
import com.ziplly.app.client.widget.NotYetLaunchedWidget;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class SignupView extends Composite implements
		ISignupView<SignupActivityPresenter>, LoginAwareView {
	private static SignupViewUiBinder uiBinder = GWT
			.create(SignupViewUiBinder.class);

	interface SignupViewUiBinder extends UiBinder<Widget, SignupView> {
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}
	
	boolean checkEmailInvitationStatus = false;
	boolean isServiceAvailable;
	private NeighborhoodDTO selectedNeighborhood;
	private List<NeighborhoodDTO> neighborhoods;
	
	@UiField
	TextBox firstname;
	@UiField
	ControlGroup firstnameCg;
	@UiField
	HelpInline firstnameError;

	@UiField
	TextBox lastname;
	@UiField
	ControlGroup lastnameCg;
	@UiField
	HelpInline lastnameError;

	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;

	@UiField
	ControlGroup neighborhoodCg;
	@UiField
	Controls neighborhoodControl;
	@UiField
	HTMLPanel neighborhoodListPanel;
	@UiField
	HelpInline neighborhoodError;
	
	@UiField
	PasswordTextBox password;
	@UiField
	ControlGroup passwordCg;
	@UiField
	HelpInline passwordError;

	@UiField
	Alert infoField;

	@UiField
	Button signupBtn;

	@UiField
	LoginWidget loginWidget;

	@UiField
	FileUpload uploadField;

	@UiField
	FormPanel uploadForm;

	@UiField
	Button uploadBtn;

	@UiField
	Image profileImagePreview;
	
	boolean imageUploaded = false;
	String profileImageUrl;
	NeighborhoodSelectorWidget neighborhoodSelectionWidget;
	SignupActivityPresenter presenter;
	private boolean facebookRegistration;

	@Inject
	public SignupView() {
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		profileImagePreview.setUrl(ZResources.IMPL.noImage().getSafeUri());
		setupHandlers();
		neighborhoodControl.setVisible(false);
	}

	private void setupHandlers() {
		firstname.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				boolean validateName = validateName(firstname.getText(), firstnameCg,
						firstnameError);
				if (validateName) {
					firstnameCg.setType(ControlGroupType.SUCCESS);
					firstnameError.setVisible(false);
				}
			}
		});
		
		zip.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				boolean validateZip = validateZip();
				if (validateZip) {
					isServiceAvailable = true;
					setControlMessageForZipcodeField(null, false, ControlGroupType.SUCCESS);
					clearNeighborhoodSection();
					presenter.getNeighborhoodData(FieldVerifier.sanitize(zip.getText()));
				}
			}
		});

		email.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				boolean validateEmail = validateEmail();
				if (validateEmail) {
					emailCg.setType(ControlGroupType.SUCCESS);
					emailError.setVisible(false);
				}
			}
		});

		password.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				boolean validateName = validateName(password.getText(), passwordCg, passwordError);
				if (validateName) {
					passwordCg.setType(ControlGroupType.SUCCESS);
					passwordError.setVisible(false);
				}
			}
		});
	}
	
	public void clearNeighborhoodSection() {
		neighborhoodListPanel.clear();
		neighborhoodCg.setType(ControlGroupType.NONE);
		neighborhoodControl.setVisible(false);
	}
	
	public void reset() {
		uploadForm.reset();
		uploadBtn.setEnabled(false);
		infoField.setVisible(false);
	}

	public void hideProfileImagePreview() {
		profileImagePreview.setVisible(false);
	}

	public void displayProfileImagePreview(String imageUrl) {
		profileImagePreview.setUrl(imageUrl);
		profileImagePreview.setVisible(true);
		this.profileImageUrl = imageUrl;
	}

	boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			setControlMessageForZipcodeField(validateZip.getErrors().get(0).getErrorMessage(),
					true,
					ControlGroupType.ERROR);
			return false;
		}
		return true;
	}

	void setControlMessageForZipcodeField(String msg, boolean msgVisible, ControlGroupType type) {
		zipCg.setType(type);
		zipError.setText(msg);
		zipError.setVisible(msgVisible);
	}
	
	boolean validateName(String name, ControlGroup cg, HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validateName(name);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	public void verifiedEmailInvitationStatus() {
		checkEmailInvitationStatus = true;
	}
	
	boolean validateEmail() {
		String emailInput = email.getText().trim();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			return false;
		}
		
//		if (isRegistrationRescricted) {
//			String code = Window.Location.getParameter("code");
//			presenter.verifyInvitationForEmail(FieldVerifier.sanitize(email.getText()), code);
//			while (!checkEmailInvitationStatus) {
//				
//			}
//		}
		return true;
	}

	boolean validatePassword(String password, 
			ControlGroup cg,
			HelpInline helpInline) {
		ValidationResult result = FieldVerifier.validatePassword(password);
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	boolean validateInput() {
		String firstnameInput = firstname.getText().trim();
		boolean valid = true;
		valid &= validateName(firstnameInput, firstnameCg, firstnameError);

		String lastnameInput = lastname.getText().trim();
		valid &= validateName(lastnameInput, lastnameCg, lastnameError);

		valid &= validateEmail();

		valid &= validateZip();

		String passwordInput = password.getText().trim();
		valid &= validatePassword(passwordInput, passwordCg, passwordError);

		valid &= validateNeighborhood();
		
		return valid;
	}

	private boolean validateNeighborhood() {
		selectedNeighborhood = getNeighborhoodSelection();
		if (selectedNeighborhood == null) {
			neighborhoodCg.setType(ControlGroupType.ERROR);
			neighborhoodError.setText(StringConstants.NEIGHBORHOOD_NOT_SELECTED);
			neighborhoodError.setVisible(false);
			return false;
		}
		
		return true;
	}

	void resetForm() {
		resetFormFields();
		clearNeighborhoodSection();
		resetErrors();
		infoField.setVisible(false);
	}

	void resetFormFields() {
		firstname.setText("");
		lastname.setText("");
		email.setText("");
		password.setText("");
		zip.setText("");
	}

	void resetErrors() {
		firstnameCg.setType(ControlGroupType.NONE);
		firstnameError.setVisible(false);
		lastnameCg.setType(ControlGroupType.NONE);
		lastnameError.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setVisible(false);
		passwordCg.setType(ControlGroupType.NONE);
		passwordError.setVisible(false);
		neighborhoodCg.setType(ControlGroupType.NONE);
		neighborhoodError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
	}

	@UiHandler("signupBtn")
	void signup(ClickEvent event) {
		resetErrors();
		
		if (!isServiceAvailable) {
			displayMessage(StringConstants.SERVICE_NOT_AVAILABLE, AlertType.ERROR);
			return;
		}
		
		if (!validateInput()) {
			return;
		}
		infoField.setType(AlertType.SUCCESS);
		String firstnameInput = firstname.getText().trim();
		String lastnameInput = lastname.getText().trim();
		String emailInput = email.getText().trim();
		String zipInput = zip.getText().trim();
		PersonalAccountDTO account = new PersonalAccountDTO();
		account.setFirstName(firstnameInput);
		account.setLastName(lastnameInput);
		account.setStatus(AccountStatus.PENDING_ACTIVATION);
		account.setEmail(emailInput);
		account.setPassword(password.getText().trim());
		account.setZip(Integer.parseInt(zipInput));
		account.setNeighborhood(selectedNeighborhood);
		account.setRole(Role.USER);
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());
		
		if (facebookRegistration) {
			account.setFacebookRegistration(true);
		}
		
		if (imageUploaded && profileImageUrl != null) {
			account.setImageUrl(profileImageUrl);
		} 

		//
		// RESTRICT_REGISTRATION_FEATURE
		//   
		String value = System.getProperty(StringConstants.RESTRICT_REGISTRATION_FEATURE, "false");
		boolean isRegistrationRescricted = Boolean.valueOf(value);
		if (isRegistrationRescricted) {
			String code = Window.Location.getParameter("code");
			presenter.register(account, code);
		} else {
			presenter.register(account);
		}
	}

	private NeighborhoodDTO getNeighborhoodSelection() {
		int count = neighborhoodListPanel.getWidgetCount();
		for(int i=0; i<count; i++) {
			RadioButton rb = (RadioButton) neighborhoodListPanel.getWidget(i);
			if (rb.getValue()) {
				return neighborhoods.get(i);
			}
		}
		
		return null;
	}

	public void displayAccount(PersonalAccountDTO account) {
		resetForm();
		firstname.setText(account.getFirstName());
		lastname.setText(account.getLastName());
		email.setText(account.getEmail());
		
		zip.setText(Integer.toString(account.getZip()));
		
		if (account.getImageUrl() != null) {
			imageUploaded = true;
			this.profileImageUrl = account.getImageUrl();
			profileImagePreview.setUrl(account.getImageUrl());
			profileImagePreview.setVisible(true);
		}
		
		zip.setText("");
		neighborhoodControl.setVisible(false);
		facebookRegistration = true;
	}

	@UiHandler("uploadBtn")
	void upload(ClickEvent event) {
		uploadForm.submit();
		imageUploaded = true;
	}

	@Override
	public void displayLoginErrorMessage(String msg, AlertType type) {
		loginWidget.displayMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		loginWidget.resetLoginForm();
	}

	@Override
	public void clear() {
		resetForm();
		loginWidget.clear();
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = (SignupActivityPresenter) presenter;
		loginWidget.setPresenter(presenter);
	}

	public void setImageUploadUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	public void displayMessage(String msg, AlertType type) {
		infoField.setText(msg);
		infoField.setType(type);
		infoField.setVisible(true);
	}
	
	public void clearMessage() {
		infoField.setText("");
		infoField.setVisible(false);
	}
	
	@Override
	public void displayNeighborhoods(List<NeighborhoodDTO> neighborhoods) {
		clearMessage();
		this.neighborhoods = neighborhoods;
		for(NeighborhoodDTO n : neighborhoods) {
			RadioButton rb = new RadioButton("neighborhood");
			rb.setText(n.getName());
			neighborhoodListPanel.add(rb);
		}
		neighborhoodControl.setVisible(true);
	}

	@Override
	public void displayNotYetLaunchedWidget() {
		isServiceAvailable = false;
		final NotYetLaunchedWidget widget = new NotYetLaunchedWidget();
		widget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String email = widget.getEmail();
				String postalCode = widget.getPostalCode();
				presenter.addToInviteList(email, postalCode);
			}
			
		});
		selectedNeighborhood = null;
		widget.show(true);
	}
}