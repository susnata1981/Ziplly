package com.ziplly.app.client.view.signup;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.activities.util.CommonUtil;
import com.ziplly.app.client.oauth.OAuthConfig;
import com.ziplly.app.client.oauth.OAuthFactory;
import com.ziplly.app.client.oauth.OAuthProvider;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.AboutViewSection;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.GooglePlacesWidget;
import com.ziplly.app.client.widget.NotYetLaunchedWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.Badge;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class SignupView extends AbstractView implements ISignupView<SignupActivityPresenter> {
	private static SignupViewUiBinder uiBinder = GWT.create(SignupViewUiBinder.class);

	interface SignupViewUiBinder extends UiBinder<Widget, SignupView> {
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}

	boolean checkEmailInvitationStatus = false;
	boolean isServiceAvailable = true;
	private NeighborhoodDTO selectedNeighborhood;

	@UiField
	Anchor howItWorksAnchor;

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
	ListBox genderListBox;

	@UiField
	Image neighborhoodLoadingImage;

	@UiField
	PasswordTextBox password;
	@UiField
	ControlGroup passwordCg;
	@UiField
	HelpInline passwordError;

	@UiField
	Alert infoField;

	@UiField
	Anchor loginAnchor;
	
	@UiField
	Button signupBtn;

	@UiField
	Anchor signInAnchor;

	@UiField
	Anchor facebookSignupAnchor;

	@UiField
	Button businessSignupBtn;

	// How it works section
	@UiField
	Button createProfileBtn;
	@UiField
	Button exploreBtn;
	@UiField
	Button postMessageBtn;

	@UiField
	Anchor termsOfUseAnchor;
	@UiField
	Anchor privacyPolicyAnchor;

	@UiField
	TextBox address;
	@UiField
	ControlGroup addressCg;
	@UiField
	HelpInline addressError;
	@UiField
	Anchor clearAddressAnchor;
	
	private GooglePlacesWidget placesWidget;
	
	private SignupActivityPresenter presenter;
	private boolean facebookRegistration;
	private boolean doValidation = true;
	private OAuthConfig authConfig;

	@Inject
	public SignupView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		StyleHelper.show(firstnameError.getElement(), false);
		StyleHelper.show(lastnameError.getElement(), false);
		StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		setupHandlers();
		for (Gender g : Gender.getValuesForSignup()) {
			genderListBox.addItem(basicDataFormatter.format(g, ValueType.GENDER));
		}
		
		placesWidget = new GooglePlacesWidget(address, addressCg, addressError, clearAddressAnchor, new GooglePlacesWidget.Listener() {
			
			@Override
			public void onChange(NeighborhoodDTO n) {
				if (n != null) {
					presenter.getNeighborhoodData(n);
				}
			}
		});
	}

	@Override
	public void onLoad() {
		setBackgroundImage(ZResources.IMPL.neighborhoodLargePic().getSafeUri().asString());
	}

	private void setupHandlers() {
		firstname.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (!doValidation) {
					return;
				}

				boolean validateName = validateName(firstname.getText(), firstnameCg, firstnameError);
				if (validateName) {
					firstnameCg.setType(ControlGroupType.SUCCESS);
					firstnameError.setVisible(false);
				}
			}
		});

		lastname.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (!doValidation) {
					return;
				}

				boolean validateName = validateName(lastname.getText(), lastnameCg, lastnameError);
				if (validateName) {
					lastnameCg.setType(ControlGroupType.SUCCESS);
					lastnameError.setVisible(false);
				}
			}
		});
		
		email.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (!doValidation) {
					return;
				}
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
				if (!doValidation) {
					return;
				}
				boolean validateName = validatePassword(password.getText(), passwordCg, passwordError);
				if (validateName) {
					passwordCg.setType(ControlGroupType.SUCCESS);
					passwordError.setVisible(false);
				}
			}
		});
		
		this.addDomHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signup(null);
				}
			}
		}, KeyDownEvent.getType());
	}

	public void reset() {
		infoField.setVisible(false);
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
		String firstnameInput = firstname.getText().trim();
		boolean valid = true;
		valid &= validateName(firstnameInput, firstnameCg, firstnameError);

		String lastnameInput = lastname.getText().trim();
		valid &= validateName(lastnameInput, lastnameCg, lastnameError);

		valid &= validateEmail();

		valid &= placesWidget.validateAddress();
		
		String passwordInput = password.getText().trim();
		valid &= validatePassword(passwordInput, passwordCg, passwordError);

		return valid;
	}

	void resetForm() {
		resetFormFields();
		resetErrors();
		infoField.setVisible(false);
	}

	void resetFormFields() {
		firstname.setText("");
		lastname.setText("");
		email.setText("");
		password.setText("");
	}

	void resetErrors() {
		firstnameCg.setType(ControlGroupType.NONE);
		firstnameError.setVisible(false);
		lastnameCg.setType(ControlGroupType.NONE);
		lastnameError.setVisible(false);
		passwordCg.setType(ControlGroupType.NONE);
		passwordError.setVisible(false);
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
		PersonalAccountDTO account = new PersonalAccountDTO();
		account.setFirstName(firstnameInput);
		account.setLastName(lastnameInput);
		account.setStatus(AccountStatus.PENDING_ACTIVATION);
		account.setEmail(emailInput);
		Gender selectedGender = Gender.values()[genderListBox.getSelectedIndex()];
		account.setGender(selectedGender);
		account.setPassword(password.getText().trim());
		LocationDTO location = new LocationDTO();
//		location.setNeighborhood(selectedNeighborhood);
		location.setNeighborhood(selectedNeighborhood);
		location.setType(LocationType.PRIMARY);
		location.setTimeCreated(new Date());
		location.setTimeUpdated(new Date());
		account.addLocation(location);

		account.setRole(Role.USER);
		account.setBadge(Badge.chipmunk);
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());

		if (facebookRegistration) {
			account.setFacebookRegistration(true);
		}

		// RESTRICT_REGISTRATION_FEATURE
		boolean isRegistrationRescricted = FeatureFlags.RegistrationRestcricted.isEnabled();
    
    if (isRegistrationRescricted) {
      String code = CommonUtil.getUrlParameter("code");
      presenter.register(account, code);
    } else {
      presenter.register(account);
    }
	}

	public void displayAccount(PersonalAccountDTO account) {
		resetForm();
		firstname.setText(account.getFirstName());
		lastname.setText(account.getLastName());
		email.setText(account.getEmail());

		genderListBox.setSelectedIndex(account.getGender().ordinal());
		facebookRegistration = true;
	}

	@Override
	public void clear() {
		resetForm();
		placesWidget.clear();
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = presenter;
		placesWidget.setPresenter(presenter);
	}

	@UiHandler("businessSignupBtn")
	public void businessSignup(ClickEvent event) {
		presenter.goTo(new BusinessSignupPlace());
	}

	@UiHandler("signInAnchor")
	public void signIn(ClickEvent event) {
		presenter.goTo(new LoginPlace());
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
	public void displayNotYetLaunchedWidget() {
		isServiceAvailable = false;
		doValidation = false;
		resetErrors();
		final NotYetLaunchedWidget widget = new NotYetLaunchedWidget();
		widget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String email = widget.getEmail();
				String postalCode = widget.getPostalCode();
				boolean valid = widget.validateInput();
				if (!valid) {
					return;
				}
				presenter.addToInviteList(email, Integer.parseInt(postalCode));
				widget.show(false);
			}

		});
		selectedNeighborhood = null;
		widget.show(true);
	}

	public void displayNeighborhoodListLoading(boolean display) {
		if (display) {
			neighborhoodLoadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri());
			StyleHelper.show(neighborhoodLoadingImage.getElement(), true);
		} else {
			StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		}
	}

	/**
	 * Signup/Login using facebook
	 */
	@UiHandler("facebookSignupAnchor")
	void signupUsingFacebook(ClickEvent event) {
		try {
			if (authConfig == null) {
				authConfig =
				    OAuthFactory.getAuthConfig(OAuthProvider.FACEBOOK.name(), presenter.getEnvironment());
			}
			Window.Location.replace(authConfig.getAuthorizationUrl());
		} catch (UnsupportedEncodingException e) {
			// It should never get here.
		}
	}

	/**
	 * Link within page.
	 */
	@UiHandler("howItWorksAnchor")
	void learnMore(ClickEvent event) {
		event.preventDefault();
		Element elem = DOM.getElementById("howItWorksLink");
		navigateToElement(elem);
	}

	/**
	 * Link within page.
	 */
	@UiHandler("createProfileBtn")
	void createAccount(ClickEvent event) {
		event.preventDefault();
		Element elem = DOM.getElementById("signupFormLink");
		navigateToElement(elem);
	}

	@UiHandler({ "exploreBtn", "postMessageBtn" })
	void exploreNeighborhod(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@UiHandler("privacyPolicyAnchor")
	public void privacyPolicyLinkClicked(ClickEvent event) {
		presenter.goTo(new AboutPlace(AboutViewSection.PRIVACY));
	}

	@UiHandler("termsOfUseAnchor")
	public void tosLinkClicked(ClickEvent event) {
		presenter.goTo(new AboutPlace(AboutViewSection.TOS));
	}

	@UiHandler("loginAnchor")
	public void gotoLogin(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}
	
	private void navigateToElement(Element elem) {
		if (elem != null) {
			elem.scrollIntoView();
		}
	}
	
	@Override
	public void displayNeighborhood(NeighborhoodDTO n) {
		selectedNeighborhood = n;
		String foundNeighborhoodMessage = basicDataFormatter.format(n, ValueType.FOUND_NEIGHBORHOOD_MESSAGE);
		placesWidget.setStatus(foundNeighborhoodMessage, ControlGroupType.SUCCESS);
		placesWidget.addedNewNeighborhood(n);
  }

	@Override
  public void displayNewNeighborhood(NeighborhoodDTO n) {
		selectedNeighborhood = n;
		String foundNeighborhoodMessage = basicDataFormatter.format(n, ValueType.FOUND_NEIGHBORHOOD_MESSAGE);
		placesWidget.setStatus(foundNeighborhoodMessage, ControlGroupType.SUCCESS);
		placesWidget.addedNewNeighborhood(n);
  }

	@Override
  public void displayNeighborhoodList(List<NeighborhoodDTO> foundNeighborhoods) {
		placesWidget.displayNeighborhoodList(foundNeighborhoods);
  }

	@Override
  public void displayErrorDuringNeighborhoodSelection(String failedToAddNeighborhood,
      AlertType error) {
		
		placesWidget.displayErrorDuringNeighborhoodSelection(failedToAddNeighborhood, error);
  }
}