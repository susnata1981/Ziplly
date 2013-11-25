package com.ziplly.app.client.view;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.overlay.AddressComponent;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessSignupView extends Composite implements ISignupView<SignupActivityPresenter> {
	String streetNumber;
	String streetName;
	String neighborhood;
	String city;
	String state;
	String zipCode;
	
	private static final String STREET_NUMBER_KEY = "street_number";
	private static final String ROUTE_KEY = "route";
	private static final String NEIGHBORHOOD_KEY = "neighborhood";
	private static final String LOCALITY_KEY = "locality";
	private static final String ADMINISTRATIVE_AREA_KEY = "administrative_area_level_1";
	private static final String POSTAL_CODE_KEY = "postal_code";
	private static final String INVALID_ADDRESS = "Invalid address";
	private static BusinessSignupViewUiBinder uiBinder = GWT
			.create(BusinessSignupViewUiBinder.class);

	interface BusinessSignupViewUiBinder extends UiBinder<Widget, BusinessSignupView> {
	}

	@UiField
	TextBox businessName;
	@UiField
	ControlGroup businessNameCg;
	@UiField
	HelpInline businessNameError;

	@UiField
	TextBox street1;
	@UiField
	ControlGroup street1Cg;
	@UiField
	HelpInline street1Error;

	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

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

	String profileImageUrl;
	SignupActivityPresenter presenter;

	@Inject
	public BusinessSignupView() {
		initWidget(uiBinder.createAndBindUi(this));
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		profileImagePreview.setUrl(ZResources.IMPL.noImage().getSafeUri());
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}
	
	@Override
	public void onAttach() {
		super.onAttach();
		initializePlacesApi(street1.getElement());
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

//	boolean validateZip() {
//		String zipInput = zip.getText().trim();
//		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
//		if (!validateZip.isValid()) {
//			zipCg.setType(ControlGroupType.ERROR);
//			zipError.setText(validateZip.getErrors().get(0).getErrorMessage());
//			zipError.setVisible(true);
//			return false;
//		}
//		return true;
//	}

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

	boolean validateEmail() {
		String emailInput = email.getText().trim();
		ValidationResult result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			return false;
		}
		return true;
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

	boolean validateInput() {
		String businessNameInput = businessName.getText().trim();
		boolean valid = true;
		valid &= validateName(businessNameInput, businessNameCg, businessNameError);

		String street = street1.getText().trim();
		valid &= validateAddress(street, street1Cg, street1Error);

		valid &= validateEmail();

//		valid &= validateZip();

		String passwordInput = password.getText().trim();
		valid &= validatePassword(passwordInput, passwordCg, passwordError);

//		String confirmPasswordInput = confirmPassword.getText().trim();
//		valid &= validatePassword(confirmPasswordInput, confirmPasswordCg, confirmPasswordError);
//
//		if (passwordInput != null && confirmPasswordInput != null) {
//			if (!confirmPasswordInput.equals(passwordInput)) {
//				passwordCg.setType(ControlGroupType.ERROR);
//				passwordError.setText(StringConstants.PASSWORD_MISMATCH_ERROR);
//				passwordError.setVisible(true);
//			}
//		}
		return valid;
	}

	private boolean validateAddress(String input, ControlGroup cg, HelpInline helpInline) {
		if (streetName == null || city == null || state == null || zipCode == null) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(INVALID_ADDRESS);
			helpInline.setVisible(true);
			return false;
		}
		return true;
	}

	void resetForm() {
		resetFormFields();
		resetErrors();
		infoField.setVisible(false);
	}

	void resetFormFields() {
		businessName.setText("");
		street1.setText("");
		email.setText("");
		password.setText("");
//		confirmPassword.setText("");
	}

	void resetErrors() {
		businessNameCg.setType(ControlGroupType.NONE);
		businessNameError.setVisible(false);
		street1Cg.setType(ControlGroupType.NONE);
		street1Error.setVisible(false);
//		zipCg.setType(ControlGroupType.NONE);
//		zipError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
	}

	@UiHandler("signupBtn")
	void signup(ClickEvent event) {
		resetErrors();
		if (!validateInput()) {
			return;
		}
		infoField.setType(AlertType.SUCCESS);
		String name = businessName.getText().trim();
		String streetOne = street1.getText().trim();
//		String streetTwo = street2.getText().trim();
//		String websiteUrl = website.getText().trim();
		String emailInput = email.getText().trim();
//		String zipInput = zip.getText().trim();
		BusinessAccountDTO account = new BusinessAccountDTO();
		account.setName(name);
		account.setStreet1(streetOne);
		account.setNeighborhood(neighborhood);
		account.setCity(city);
		account.setState(state);
		account.setZip(Integer.parseInt(zipCode));
//		account.setStreet2(streetTwo);
//		account.setZip(Integer.parseInt(zipInput));
//		account.setWebsite(websiteUrl);
		account.setEmail(emailInput);
		account.setPassword(password.getText().trim());
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());

		if (profileImageUrl != null) {
			account.setImageUrl(profileImageUrl);
		}

		presenter.register(account);
	}

	@UiHandler("uploadBtn")
	void upload(ClickEvent event) {
		uploadForm.submit();
	}

	@Override
	public void clear() {
		resetForm();
		resetLoginForm();
	}

	public void setImageUploadUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
		uploadBtn.setEnabled(true);
	}

	public void addUploadFormHandler(SubmitCompleteHandler submitCompleteHandler) {
		uploadForm.addSubmitCompleteHandler(submitCompleteHandler);
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = presenter;
		loginWidget.setPresenter(presenter);
	}

	@Override
	public void displayAccount(PersonalAccountDTO a) {
		throw new RuntimeException();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		loginWidget.displayMessage(msg, type);
	}

	@Override
	public void resetLoginForm() {
		loginWidget.clear();
	}

	/*
	 * Google places api
	 */
	public native void initializePlacesApi(Element elem) /*-{
		var options = {
			types : [ 'geocode' ]
		};

		var autocomplete = new $wnd.google.maps.places.Autocomplete(elem,
				options);

		var componentForm = {
			street_number : 'short_name',
			route : 'long_name',
			locality : 'long_name',
			administrative_area_level_1 : 'short_name',
			country : 'long_name',
			postal_code : 'short_name'
		};
		var that = this;
		$wnd.google.maps.event
				.addListener(
						autocomplete,
						'place_changed',
						function() {
							var places = autocomplete.getPlace();
							console.log(places);
							$wnd.places = autocomplete.getPlace();
							that.@com.ziplly.app.client.view.BusinessSignupView::populateAddressFields()();
						});
	}-*/;

	public void populateAddressFields() {
		System.out.println("populateAddressFields called.");
		JsArray<AddressComponent> components = getAddressComponents();
		for (int i = 0; i < components.length(); i++) {
			AddressComponent ac = components.get(i);
			System.out.println(ac.getType()+"-->"+ac.getShortName());
			if (ac.getType().equals(STREET_NUMBER_KEY)) {
				streetNumber = ac.getLongName();
			} 
			else if (ac.getType().equals(ROUTE_KEY)) {
				streetName = ac.getLongName();
			} 
			else  if (ac.getType().equals(NEIGHBORHOOD_KEY)) {
				neighborhood = ac.getLongName();
			} 
			else  if (ac.getType().equals(LOCALITY_KEY)) {
				city = ac.getLongName();
			} 
			else  if (ac.getType().equals(ADMINISTRATIVE_AREA_KEY)) {
				state = ac.getLongName();
			}
			else if (ac.getType().equals(POSTAL_CODE_KEY)) {
//				zip.setText(ac.getLongName());
				zipCode = ac.getLongName();
			}
		}
	}

	public native JsArray<AddressComponent> getAddressComponents() /*-{
		return $wnd.places.address_components;
	}-*/;

}
