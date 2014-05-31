package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.googlecode.gwt.charts.client.event.OnMouseOverEvent;
import com.googlecode.gwt.charts.client.event.OnMouseOverHandler;
import com.ziplly.app.client.activities.SignupActivityPresenter;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.GooglePlacesWidget;
import com.ziplly.app.client.widget.NeighborhoodSelectorWidget;
import com.ziplly.app.client.widget.NotYetLaunchedWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessCategory;
import com.ziplly.app.model.BusinessPropertiesDTO;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PriceRange;
import com.ziplly.app.model.Role;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessSignupView extends AbstractView implements
    ISignupView<SignupActivityPresenter> {
	String streetNumber;
	String streetName;
	String neighborhood;
	String city;
	String state;
	String zipCode;
	NeighborhoodSelectorWidget neighborhoodSelectionWidget;
	NeighborhoodDTO selectedNeighborhood;
	boolean isServiceAvailable = true;
	boolean doValidation = true;

	private static final String START_TIME = "9AM";
	private static final String END_TIME = "9PM";
	private static BusinessSignupViewUiBinder uiBinder = GWT.create(BusinessSignupViewUiBinder.class);

	interface BusinessSignupViewUiBinder extends UiBinder<Widget, BusinessSignupView> {
	}

//	@UiField
//	HTMLPanel createHelpPanel;
//	@UiField
//	Element createDetailsUl;
	
	@UiField
	TextBox businessName;
	@UiField
	ControlGroup businessNameCg;
	@UiField
	HelpInline businessNameError;

	@UiField
	ListBox businessCategory;

	@UiField
	TextBox street1;
	@UiField
	ControlGroup street1Cg;
	@UiField
	HelpInline street1Error;
	@UiField
	Image neighborhoodLoadingImage;

	@UiField
	ControlGroup phoneCg;
	@UiField
	HelpInline phoneError;
	@UiField
	TextBox phone;

	@UiField
	ControlGroup neighborhoodCg;
	@UiField
	Controls neighborhoodControl;
	@UiField
	HTMLPanel neighborhoodListPanel;
	@UiField
	HelpInline neighborhoodError;

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
	Anchor signInAnchor;

	// how it works section
	@UiField
	Anchor learnMoreAnchor;
	@UiField
	Button createProfileBtn;
	@UiField
	Button exploreBtn;
	@UiField
	Button postMessageBtn;

	@UiField
	Anchor privacyPolicyAnchor;
	@UiField
	Anchor termsOfUseAnchor;

	@UiField
	Anchor clearAddressAnchor;
	
	SignupActivityPresenter presenter;
	private GooglePlacesWidget placesWidget;
	
	@Inject
	public BusinessSignupView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		neighborhoodControl.setVisible(false);
		placesWidget = new GooglePlacesWidget(street1, street1Cg, street1Error, clearAddressAnchor, new GooglePlacesWidget.Listener() {

			@Override
      public void onChange(NeighborhoodDTO n) {
				presenter.getNeighborhoodData(n);
      }
		});
		
		populateBusinessCategory();
		StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		setupUi();
		setupHandlers();
	}

	private void setupUi() {
//	  StyleHelper.show(createDetailsUl, false);
//	  createHelpPanel.addDomHandler(new MouseOverHandler() {
//
//      @Override
//      public void onMouseOver(MouseOverEvent event) {
//        StyleHelper.show(createDetailsUl, true);
//      }
//      
//    }, MouseOverEvent.getType());
//	  
//	  createHelpPanel.addDomHandler(new MouseOutHandler() {
//
//      @Override
//      public void onMouseOut(MouseOutEvent event) {
//        StyleHelper.show(createDetailsUl, false);
//      }
//    }, MouseOutEvent.getType());
  }

  private void populateBusinessCategory() {
		for (BusinessCategory category : BusinessCategory.values()) {
			businessCategory.addItem(category.getName());
		}
	}

	@Override
	public void onLoad() {
		setBackgroundImage(ZResources.IMPL.neighborhoodLargePic().getSafeUri().asString());
	}

	private void setupHandlers() {
		businessName.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (!doValidation) {
					return;
				}

				boolean validateName =
				    validateName(businessName.getText(), businessNameCg, businessNameError);
				if (validateName) {
					businessNameCg.setType(ControlGroupType.SUCCESS);
					businessNameError.setVisible(false);
				}
			}
		});

		email.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (!doValidation) {
					return;
				}

				boolean validateName = validateName(email.getText(), emailCg, emailError);
				if (validateName) {
					emailCg.setType(ControlGroupType.SUCCESS);
					emailError.setVisible(false);
				}
			}
		});

		phone.addBlurHandler(new BlurHandler() {

      @Override
      public void onBlur(BlurEvent event) {
        if (!doValidation) {
          return;
        }
        
        boolean valid = validatePhone();
        if (valid) {
          phoneCg.setType(ControlGroupType.SUCCESS);
          phoneError.setVisible(false);
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

	void validateAddressField() {
		doValidation = true;
		boolean validateAddress = validateAddress(street1.getText(), street1Cg, street1Error);
		if (validateAddress) {
			// reset isServiceAvailable
			isServiceAvailable = true;
			// Call presenter to get Neighborhood data
			presenter.getNeighborhoodData(FieldVerifier.sanitize(zipCode));
			street1Cg.setType(ControlGroupType.SUCCESS);
			street1Error.setVisible(false);
		}
	}

	@UiFactory
	ZResources resources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}

	@Override
	public void onAttach() {
		super.onAttach();
//		initializePlacesApi(street1.getElement());
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

//		String street = street1.getText().trim();
//		valid &= validateAddress(street, street1Cg, street1Error);
		
		valid &= placesWidget.validateAddress();
//			valid &= (selectedNeighborhood != null);
			
//		valid &= validateNeighborhood();

		valid &= validateEmail();

		valid &= validatePhone();

		String passwordInput = password.getText().trim();
		valid &= validatePassword(passwordInput, passwordCg, passwordError);

		return valid;
	}

	private boolean validatePhone() {
		String phoneNumber = phone.getText().trim();
		ValidationResult result = FieldVerifier.validatePhone(phoneNumber);
		if (!result.isValid()) {
			phoneCg.setType(ControlGroupType.ERROR);
			phoneError.setText(result.getErrors().get(0).getErrorMessage());
			phoneError.setVisible(true);
			return false;
		}
		return true;
	}

	private boolean validateAddress(String input, ControlGroup cg, HelpInline helpInline) {
		if (streetName == null || city == null || state == null || zipCode == null) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(StringConstants.INVALID_ADDRESS);
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
		phone.setText("");
		password.setText("");
		neighborhoodListPanel.clear();
	}

	void resetErrors() {
		businessNameCg.setType(ControlGroupType.NONE);
		businessNameError.setVisible(false);
		street1Cg.setType(ControlGroupType.NONE);
		street1Error.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
		phoneCg.setType(ControlGroupType.NONE);
		phoneError.setVisible(false);
		neighborhoodCg.setType(ControlGroupType.NONE);
		neighborhoodError.setVisible(false);
		passwordCg.setType(ControlGroupType.NONE);
		passwordError.setVisible(false);
	}

	@UiHandler("signupBtn")
	void signup(ClickEvent event) {
		resetErrors();

		// checks to see if ziplly is available in the area code.
		// this is set inside displayNotYetLaunchedWidget function.
		if (!isServiceAvailable) {
			displayMessage(StringConstants.SERVICE_NOT_AVAILABLE, AlertType.ERROR);
			return;
		}

		if (!validateInput()) {
			return;
		}
		infoField.setType(AlertType.SUCCESS);
		String name = businessName.getText().trim();
		String streetOne = street1.getText().trim();
		String emailInput = email.getText().trim();
		BusinessAccountDTO account = new BusinessAccountDTO();
		account.setName(name);
		account.setPhone(FieldVerifier.getEscapedText(phone.getText()));

		LocationDTO location = new LocationDTO();
		location.setNeighborhood(selectedNeighborhood);
		location.setAddress(streetOne);
		location.setType(LocationType.PRIMARY);
		location.setTimeUpdated(new Date());
		location.setTimeCreated(new Date());
		account.addLocation(location);

		// this should go away.
		// account.setNeighborhood(selectedNeighborhood);
		// account.setCity(city);
		// account.setState(state);
		// account.setZip(Integer.parseInt(zipCode));

		// business category
		BusinessCategory category = BusinessCategory.values()[businessCategory.getSelectedIndex()];
		account.setCategory(category);

		// Hardcoded to COMMERCIAL for now (Need admin privileges to change it for
		// public institutions)
		account.setBusinessType(BusinessType.COMMERCIAL);

		account.setRole(Role.USER);
		account.setEmail(emailInput);
		account.setStatus(AccountStatus.PENDING_ACTIVATION);
		account.setPassword(password.getText().trim());
		account.setLastLoginTime(new Date());
		account.setTimeCreated(new Date());

		account.setProperties(getDefaultProperties());

		String value = System.getProperty(StringConstants.RESTRICT_REGISTRATION_FEATURE, "false");
		boolean isRegistrationRescricted = Boolean.valueOf(value);
		if (isRegistrationRescricted) {
			String code = Window.Location.getParameter("code");
			presenter.register(account, code);
		} else {
			presenter.register(account);
		}
	}

//	private NeighborhoodDTO getNeighborhoodSelection() {
//		int count = neighborhoodListPanel.getWidgetCount();
//		for (int i = 0; i < count; i++) {
//			RadioButton rb = (RadioButton) neighborhoodListPanel.getWidget(i);
//			if (rb.getValue()) {
//				return neighborhoods.get(i);
//			}
//		}
//
//		return null;
//	}

	private BusinessPropertiesDTO getDefaultProperties() {
		BusinessPropertiesDTO properties = new BusinessPropertiesDTO();
		properties.setAcceptsCreditCard(true);
		properties.setMondayStartTime(START_TIME);
		properties.setMondayEndTime(END_TIME);
		properties.setTuesdayStartTime(START_TIME);
		properties.setTuesdayEndTime(END_TIME);
		properties.setWednesdayStartTime(START_TIME);
		properties.setWednesdayEndTime(END_TIME);
		properties.setThursdayStartTime(START_TIME);
		properties.setThursdayEndTime(END_TIME);
		properties.setFridayStartTime(START_TIME);
		properties.setFridayEndTime(END_TIME);
		properties.setSaturdayStartTime(START_TIME);
		properties.setSaturdayEndTime(END_TIME);
		properties.setSundayStartTime(START_TIME);
		properties.setSundayEndTime(END_TIME);

		properties.setPriceRange(PriceRange.MEDIUM);
		properties.setWifiAvailable(false);
		properties.setParkingAvailable(true);
		properties.setPriceRange(PriceRange.MEDIUM);
		return properties;
	}

	@Override
	public void clear() {
		resetForm();
		placesWidget.clear();
	}

	public void clearMessage() {
		infoField.setText("");
		infoField.setVisible(false);
	}

	public void clearNeighborhoodSection() {
		neighborhoodListPanel.clear();
		neighborhoodCg.setType(ControlGroupType.NONE);
		neighborhoodControl.setVisible(false);
	}

	@Override
	public void setPresenter(SignupActivityPresenter presenter) {
		this.presenter = presenter;
		placesWidget.setPresenter(presenter);
	}

	@Override
	public void displayAccount(PersonalAccountDTO a) {
		throw new RuntimeException();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		infoField.setText(msg);
		infoField.setType(type);
		infoField.setVisible(true);
	}

	/**
	 * Displays a modal window to get user information
	 */
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
				presenter.addToInviteList(email, Integer.parseInt(postalCode));
			}

		});
		selectedNeighborhood = null;
		widget.show(true);
	}

	@Override
	public void displayNeighborhoodListLoading(boolean display) {
		if (display) {
			neighborhoodLoadingImage.setUrl(ZResources.IMPL.loadingImageSmall().getSafeUri());
			StyleHelper.show(neighborhoodLoadingImage.getElement(), true);
		} else {
			StyleHelper.show(neighborhoodLoadingImage.getElement(), false);
		}
	}

	@UiHandler("signInAnchor")
	public void signIn(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@UiHandler("learnMoreAnchor")
	public void learnMore(ClickEvent event) {
		event.preventDefault();
		Element elem = DOM.getElementById("howItWorksLink");
		navigateToElement(elem);
	}

	@UiHandler("createProfileBtn")
	public void createProfile(ClickEvent event) {
		event.preventDefault();
		Element elem = DOM.getElementById("signupFormLink");
		navigateToElement(elem);
	}

	@UiHandler("postMessageBtn")
	public void postMessage(ClickEvent event) {
		presenter.goTo(new LoginPlace());
	}

	@UiHandler("exploreBtn")
	public void explore(ClickEvent event) {
		presenter.goTo(new BusinessAccountSettingsPlace());
	}

	private void navigateToElement(Element elem) {
		if (elem != null) {
			elem.scrollIntoView();
		}
	}

	@UiHandler("privacyPolicyAnchor")
	public void privacyPolicyLinkClicked(ClickEvent event) {
		presenter.goTo(new AboutPlace(AboutViewSection.PRIVACY));
	}

	@UiHandler("termsOfUseAnchor")
	public void tosLinkClicked(ClickEvent event) {
		presenter.goTo(new AboutPlace(AboutViewSection.TOS));
	}

	@Deprecated
	@Override
  public void displayNeighborhoods(List<NeighborhoodDTO> neighbordhoods) {
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
	
	@UiField
	Anchor pricingAnchor;
	
	@UiHandler("pricingAnchor")
	public void click(ClickEvent event) {
	  Element elem = DOM.getElementById("pricingPlans");
	  navigateToElement(elem);
	}
}
