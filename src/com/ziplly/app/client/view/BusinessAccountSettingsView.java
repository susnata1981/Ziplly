package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity.IBusinessAccountSettingView;
import com.ziplly.app.client.activities.util.PaymentFlow;
import com.ziplly.app.client.resource.TableResources;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.business.SubscriptionPlansView;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.AddLocationModal;
import com.ziplly.app.client.widget.HPanel;
import com.ziplly.app.client.widget.MessageModal;
import com.ziplly.app.client.widget.PricingPlanWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.SubscriptionPlanWidget;
import com.ziplly.app.client.widget.PricingPlanWidget.TITLE_HUE;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessCategory;
import com.ziplly.app.model.BusinessPropertiesDTO;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.Cuisine;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NotificationAction;
import com.ziplly.app.model.PriceRange;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.SubscriptionPlanType;
import com.ziplly.app.model.overlay.SubscriptionDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.ValidationResult;

public class BusinessAccountSettingsView extends AbstractView implements
    IBusinessAccountSettingView {

	private static final int MAX_TRANSACTION_TABLE_ROW_COUNT = 100;
	private static final String GOOGLE_MAPS_LOCATION = "http://maps.google.com?q=";
	BasicDataFormatter basicDataFormatter = (BasicDataFormatter) AbstractValueFormatterFactory
	    .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
	TableResources tableResources;

	private static BusinessAccountSettingsViewUiBinder uiBinder = GWT
	    .create(BusinessAccountSettingsViewUiBinder.class);

	public interface Style extends CssResource {
		String subscriptionPlanWidget();

		String subscriptionPlanTablePanelMessage();

		String subscriptionPlanTable();

		String row();

		String heading();

		String hpanelNoMargin();

		String locationBlock();

		String tinyText();

		String locationItemPanel();
	}

	public static interface BusinessAccountSettingsPresenter extends
	    AccountSettingsPresenter<BusinessAccountDTO> {

		void checkSubscriptionEligibility(Long subscriptionId);

		void getNeighborhoodData(String zip);

		void updateLocation(BusinessAccountDTO account);
	}

	interface BusinessAccountSettingsViewUiBinder extends
	    UiBinder<Widget, BusinessAccountSettingsView> {
	}

	@UiField
	Style style;

	@UiField
	Alert message;

	@UiField
	TextBox businessName;
	@UiField
	ControlGroup businessNameCg;
	@UiField
	HelpInline businessNameError;

	@UiField
	TextBox neighborhoodTextBox;

	@UiField
	TextBox street1;
	@UiField
	ControlGroup street1Cg;
	@UiField
	HelpInline street1Error;

	@UiField
	TextBox phone;
	@UiField
	ControlGroup phoneCg;
	@UiField
	HelpInline phoneError;

	@UiField
	TextBox website;
	@UiField
	ControlGroup websiteCg;
	@UiField
	HelpInline websiteError;

	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;

	@UiField
	ListBox businessCategory;

	@UiField
	Controls cuisineControls;
	@UiField
	ListBox cuisineListBox;

	@UiField
	ListBox priceRangeListBox;
	@UiField
	CheckBox wifiAvailableCheckbox;
	@UiField
	CheckBox parkingAvailableCheckbox;

	@UiField
	FormPanel uploadForm;
	@UiField
	Image uploadAnchorIcon;
	@UiField
	FileUpload uploadField;
	@UiField
	HTMLPanel businessProfileImagePanel;
	@UiField
	Image profileImagePreview;
	private boolean imageUploaded;

	// Hours of operation
	@UiField
	TextBox mondayStart;
	@UiField
	TextBox mondayEnd;
	@UiField
	TextBox tuesdayStart;
	@UiField
	TextBox tuesdayEnd;
	@UiField
	TextBox wednesdayStart;
	@UiField
	TextBox wednesdayEnd;
	@UiField
	TextBox thursdayStart;
	@UiField
	TextBox thursdayEnd;
	@UiField
	TextBox fridayStart;
	@UiField
	TextBox fridayEnd;
	@UiField
	TextBox saturdayStart;
	@UiField
	TextBox saturdayEnd;
	@UiField
	TextBox sundayStart;
	@UiField
	TextBox sundayEnd;

	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;

	// Payment details tab
	@UiField
	Alert paymentStatus;
	@UiField
	HTMLPanel subscriptionPlanTablePanel;

	// Notification Panel
	@UiField
	HTMLPanel notificationPanel;

	// transaction details elements
	CellTable<SubscriptionDTO> transactionTable;
	@UiField
	Tab subscriptionTab;
	@UiField
	HTMLPanel transactionDetailsPanel;

	// Location tab
	@UiField
	HTMLPanel locationPanel;
	@UiField
	Anchor addLocationAnchor;
	private AddLocationModal addLocationModal;

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

	private CellTable<SubscriptionPlanDTO> subscriptionPlanTable;
	private BusinessAccountDTO account;

	private BusinessAccountSettingsPresenter presenter;

	private Map<Long, SubscriptionPlanWidget> subscriptionPlanWidgetMap =
	    new HashMap<Long, SubscriptionPlanWidget>();
	private List<SubscriptionDTO> transactions = new ArrayList<SubscriptionDTO>();
	private List<SubscriptionPlanDTO> subscriptionPlans;
	private Map<AccountNotificationSettingsDTO, ListBox> accountNotificationSettingsMap =
	    new HashMap<AccountNotificationSettingsDTO, ListBox>();

	@Inject
	public BusinessAccountSettingsView(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		clearPaymentStatus();
		StyleHelper.show(cuisineControls.getElement(), false);
		StyleHelper.show(uploadField.getElement(), false);
		uploadAnchorIcon.setUrl(ZResources.IMPL.uploadIcon().getSafeUri());
		// Disable add location for now
		addLocationAnchor.setVisible(false);
		neighborhoodTextBox.setReadOnly(true);
		setupHandlers();
	}

	private void setupHandlers() {
		businessCategory.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				BusinessCategory category = BusinessCategory.values()[businessCategory.getSelectedIndex()];
				// Display cuisine listbox only for restaurants
				if (category == BusinessCategory.RESTAURANT) {
					StyleHelper.show(cuisineControls.getElement(), true);
				} else {
					StyleHelper.show(cuisineControls.getElement(), false);
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
				clearError();
				onUpload();
			}
		});
	}

	@Override
	public void clear() {
		this.account = null;
		message.clear();
		message.setVisible(false);
		businessName.setText("");
		street1.setText("");
		phone.setText("");
		website.setText("");
		email.setText("");
		clearPaymentStatus();
		StyleHelper.clearBackground();
	}

	@Override
	public void displaySettings(BusinessAccountDTO account) {
		resetUploadForm();
		if (account != null) {
			this.account = account;
			businessName.setText(account.getName());
			street1.setText(account.getCurrentLocation().getAddress());

			website.setText(account.getWebsite());
			email.setText(account.getEmail());
			phone.setText(account.getPhone());
			neighborhoodTextBox.setText(basicDataFormatter.format(account
          .getCurrentLocation()
          .getNeighborhood(), ValueType.NEIGHBORHOOD));

			businessCategory.clear();
			for (BusinessCategory category : BusinessCategory.values()) {
				businessCategory.addItem(category.name());
			}

			if (account.getCategory() != null) {
				businessCategory.setSelectedIndex(account.getCategory().ordinal());
			}

			for (PriceRange range : PriceRange.values()) {
				priceRangeListBox.addItem(range.name());
			}

			for (Cuisine cuisine : Cuisine.values()) {
				cuisineListBox.addItem(cuisine.name());
			}

			displayBusinessProperties();

			setProfileImage(basicDataFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
			
			// Don't show subscription tab for non-profits
			if (account.getBusinessType() == BusinessType.NON_PROFIT) {
				hideSubscriptionTab();
			}

			populateLocationPanel();
			popoulateNotificationSettings(account);
		}
	}

	private void populateLocationPanel() {
		locationPanel.clear();
		for (LocationDTO location : account.getLocations()) {
			Panel panel = addLocationToPanel(location);
			locationPanel.add(panel);
		}
	}

	private Panel addLocationToPanel(final LocationDTO location) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(style.locationItemPanel());
		HPanel hpanel = new HPanel();
		hpanel.setStyleName(style.hpanelNoMargin());
		Label locationName =
		    new Label(basicDataFormatter.format(location.getNeighborhood(), ValueType.NEIGHBORHOOD));
		locationName.addStyleName(style.row());
		locationName.addStyleName(style.heading());
		hpanel.add(locationName);

		if (location.getType() == LocationType.PRIMARY) {
			Popover primaryLocationPopover = new Popover();
			Anchor learnMoreAnchor = new Anchor(StringConstants.PRIMARY_LOCATION_KEY);
			learnMoreAnchor.addStyleName(style.tinyText());
			primaryLocationPopover.add(learnMoreAnchor);
			primaryLocationPopover.setHeading(StringConstants.LEARN_MORE_TEXT);
			primaryLocationPopover.setText(StringConstants.PRIMARY_LOCATION);
			hpanel.add(primaryLocationPopover);
		}

		Anchor mapAnchor = new Anchor("map");
		mapAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				Window.open(getAddressUrl(location.getAddress()), "_blank", "");
			}

		});

		mapAnchor.setHref(getAddressUrl(location.getAddress()));
		hpanel.add(mapAnchor);

		Anchor removeAnchor = new Anchor("remove");
		removeAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (location.getType() == LocationType.PRIMARY) {
					displayMessage(StringConstants.CANT_REMOVE_PRIMARY_LOCATION, AlertType.ERROR);
					return;
				}
				account.getLocations().remove(location);
				presenter.save(account);
			}
		});
		hpanel.add(removeAnchor);
		panel.add(hpanel);

		hpanel = new HPanel();
		hpanel.setStyleName(style.hpanelNoMargin());
		Label locationAddress =
		    new Label(basicDataFormatter.format(location.getAddress(), ValueType.ADDRESS));
		hpanel.add(locationAddress);
		hpanel.addStyleName(style.row());
		panel.add(hpanel);
		panel.addStyleName(style.locationBlock());
		return panel;
	}

	// HARD CODED - NEEDS TO CHANGE
	private String getAddressUrl(String address) {
		return GOOGLE_MAPS_LOCATION + address;
	}

	private void displayBusinessProperties() {
		BusinessPropertiesDTO props = account.getProperties();

		if (props.getPriceRange() != null) {
			priceRangeListBox
			    .setSelectedIndex(PriceRange.valueOf(props.getPriceRange().name()).ordinal());
		}
		wifiAvailableCheckbox.setValue(props.getWifiAvailable());
		parkingAvailableCheckbox.setValue(props.isParkingAvailable());

		mondayStart.setText(props.getMondayStartTime());
		mondayEnd.setText(props.getMondayEndTime());

		tuesdayStart.setText(props.getTuesdayStartTime());
		tuesdayEnd.setText(props.getTuesdayEndTime());

		wednesdayStart.setText(props.getWednesdayStartTime());
		wednesdayEnd.setText(props.getWednesdayEndTime());

		thursdayStart.setText(props.getThursdayEndTime());
		thursdayEnd.setText(props.getThursdayEndTime());

		fridayStart.setText(props.getFridayStartTime());
		fridayEnd.setText(props.getFridayEndTime());

		saturdayStart.setText(props.getSaturdayStartTime());
		saturdayEnd.setText(props.getSaturdayEndTime());

		sundayStart.setText(props.getSundayStartTime());
		sundayEnd.setText(props.getSundayEndTime());
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
	 */
	private void popoulateNotificationSettings(AccountDTO account) {
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
			HTMLPanel span =
			    new HTMLPanel(basicDataFormatter.format(ans.getType(), ValueType.NOTIFICATION_TYPE));
			span.setWidth("120px");
			panel.add(span);
			panel.add(action);
			notificationPanel.add(panel);
			accountNotificationSettingsMap.put(ans, action);
		}
	}

	boolean validatePhone() {
		String phoneInput = phone.getText().trim();
		ValidationResult validatePhone = FieldVerifier.validatePhone(phoneInput);
		if (!validatePhone.isValid()) {
			phoneCg.setType(ControlGroupType.ERROR);
			phoneError.setText(validatePhone.getErrors().get(0).getErrorMessage());
			phoneError.setVisible(true);
			return false;
		}
		return true;
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

	boolean validateInput() {
		String businessNameInput = businessName.getText().trim();
		boolean valid = true;
		valid &= validateName(businessNameInput, businessNameCg, businessNameError);

		String lastnameInput = street1.getText().trim();
		valid &= validateName(lastnameInput, street1Cg, street1Error);

		valid &= validateEmail();

		valid &= validatePhone();

		return valid;
	}

	void resetErrors() {
		businessNameCg.setType(ControlGroupType.NONE);
		businessNameError.setVisible(false);
		street1Cg.setType(ControlGroupType.NONE);
		street1Error.setVisible(false);
		phoneCg.setType(ControlGroupType.NONE);
		phoneError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		emailError.setVisible(false);
	}

	@Override
	public void onSave() {
		resetErrors();
		if (!validateInput()) {
			return;
		}

		String name = businessName.getText().trim();
		// String streetOne = street1.getText().trim();
		String websiteUrl = website.getText().trim();
		String emailInput = email.getText().trim();
		String phoneInput = phone.getText().trim();
		BusinessAccountDTO acct = new BusinessAccountDTO();
		acct.setAccountId(account.getAccountId());

		if (imageUploaded) {
			acct.getImages().add(currentUploadedImage);
			// acct.setImageUrl(profileImagePreview.getUrl());
		}

		acct.setUid(account.getUid());
		acct.setName(name);
		acct.setPhone(phoneInput);
		// acct.setStreet1(streetOne);
		acct.setStatus(account.getStatus());
		acct.setRole(account.getRole());
		acct.setLocations(account.getLocations());
		acct.setWebsite(websiteUrl);
		acct.setEmail(emailInput);
		acct.setLastLoginTime(account.getLastLoginTime());
		acct.setCategory(BusinessCategory.values()[businessCategory.getSelectedIndex()]);

		BusinessPropertiesDTO props = new BusinessPropertiesDTO();
		PriceRange priceRange = PriceRange.values()[priceRangeListBox.getSelectedIndex()];
		props.setPriceRange(priceRange);

		if (StyleHelper.isVisible(cuisineControls.getElement())) {
			Cuisine cuisine = Cuisine.values()[cuisineListBox.getSelectedIndex()];
			props.setCuisine(cuisine);
		}

		props.setWifiAvailable(wifiAvailableCheckbox.getValue());
		props.setParkingAvailable(parkingAvailableCheckbox.getValue());
		props.setMondayStartTime(FieldVerifier.sanitize(mondayStart.getText()));
		props.setMondayEndTime(FieldVerifier.sanitize(mondayEnd.getText()));
		props.setTuesdayStartTime(FieldVerifier.sanitize(tuesdayStart.getText()));
		props.setTuesdayEndTime(FieldVerifier.sanitize(tuesdayEnd.getText()));
		props.setWednesdayStartTime(FieldVerifier.sanitize(wednesdayStart.getText()));
		props.setWednesdayEndTime(FieldVerifier.sanitize(wednesdayEnd.getText()));
		props.setThursdayStartTime(FieldVerifier.sanitize(thursdayStart.getText()));
		props.setThursdayEndTime(FieldVerifier.sanitize(thursdayEnd.getText()));
		props.setFridayStartTime(FieldVerifier.sanitize(fridayStart.getText()));
		props.setFridayEndTime(FieldVerifier.sanitize(fridayEnd.getText()));
		props.setSaturdayStartTime(FieldVerifier.sanitize(saturdayStart.getText()));
		props.setSaturdayEndTime(FieldVerifier.sanitize(saturdayEnd.getText()));
		props.setSundayStartTime(FieldVerifier.sanitize(sundayStart.getText()));
		props.setSundayEndTime(FieldVerifier.sanitize(sundayEnd.getText()));

		acct.setProperties(props);
		
		presenter.save(acct);
		saveBtn.setEnabled(false);
		showSaveButton(false);
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
	public void clearError() {
		message.setText("");
		message.setVisible(false);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
//		message.setType(type);
//		message.setText(msg);
//		message.setVisible(true);
		MessageModal modal = new MessageModal();
		modal.setContent(msg);
		modal.show();
	}

	@UiHandler("saveBtn")
	public void save(ClickEvent event) {
		onSave();
	}

	@UiHandler("cancelBtn")
	public void cancel(ClickEvent event) {
		onCancel();
	}

	@Override
	public void onCancel() {
		presenter.cancel();
	}

	@UiField
	FluidContainer settingsPanel;
	private ImageDTO currentUploadedImage;

	@Override
	public void displayImagePreview(String imageUrl) {
		if (imageUrl != null) {
			currentUploadedImage = ImageUtil.parseImageUrl(imageUrl);
			profileImagePreview.setUrl(currentUploadedImage.getUrl() + "=s1600");
			StyleHelper.setBackgroundImage(RootPanel.get().getElement(), currentUploadedImage.getUrl());
			eventBus.fireEvent(new LoadingEventEnd());
		}
	}

	private void setProfileImage(String imageUrl) {
		profileImagePreview.setUrl(imageUrl);
//		StyleHelper.setBackgroundImage(RootPanel.get().getElement(), imageUrl);
	}

	@Override
	public void resetUploadForm() {
		uploadForm.setAction("");
	}

	@Override
	public void onUpload() {
		uploadForm.submit();
		imageUploaded = true;
		eventBus.fireEvent(new LoadingEventStart());
	}

	@Override
	public void displayPaymentStatus(String msg, AlertType type) {
		paymentStatus.setVisible(true);
		paymentStatus.setText(msg);
		paymentStatus.setType(type);
	}

	@Override
	public void clearPaymentStatus() {
		paymentStatus.setText("");
		paymentStatus.setVisible(false);
	}

	public void initiatePay(Long subscriptionPlanId, String token) {
	  PaymentFlow flow = new PaymentFlow(null);
	  flow.doPay(token);
	}
	
	@Override
	public void setPresenter(BusinessAccountSettingsPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans) {
	  subscriptionPlanTablePanel.clear();
	  HorizontalPanel panel = new HorizontalPanel();
	  SubscriptionDTO activePlan = account.getLatestSubscription();
	  
	  for(SubscriptionPlanDTO plan : plans) {
	    PricingPlanWidget widget = createPricingPlanWidget(plan);
	    if (plan.getPlanType() == SubscriptionPlanType.BASIC) {
	      widget.hideFees();
	    }
	    
	    if (activePlan != null) {
	      if (activePlan.getSubscriptionPlan().getPlanType() == SubscriptionPlanType.BASIC) {
	        if (plan.getPlanType() != SubscriptionPlanType.BASIC) {
	          widget.getChoosePlanButton().setText("Upgrade");
	        } 
	      }
	      
	      if (plan.getPlanType() == activePlan.getSubscriptionPlan().getPlanType()) {
          widget.getChoosePlanButton().setEnabled(false);
        }
	    }
	    panel.add(widget);
	  }
	  subscriptionPlanTablePanel.add(panel);
	}

	private PricingPlanWidget createPricingPlanWidget(final SubscriptionPlanDTO plan) {
	  PricingPlanWidget widget = new PricingPlanWidget();
    widget.setTitle(plan.getName());
    widget.setTweetCount(Integer.toString(plan.getTweetsAllowed()));
    widget.setCouponCount(Integer.toString(plan.getCouponsAllowed()));
    widget.setPrice(basicDataFormatter.format(plan.getFee(), ValueType.PRICE));
    
    if (plan.getPlanType() == SubscriptionPlanType.PREMIUM) {
      widget.setTitleHue(TITLE_HUE.GREEN);
    }
    
    widget.getChoosePlanButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        presenter.checkSubscriptionEligibility(plan.getSubscriptionId());
      }
    });
    
    return widget;
	}
	
	private CellTable<SubscriptionDTO> buildTransactionTable() {
		tableResources = GWT.create(TableResources.class);
		tableResources.cellTableStyle().ensureInjected();

		CellTable<SubscriptionDTO> table =
		    new CellTable<SubscriptionDTO>(MAX_TRANSACTION_TABLE_ROW_COUNT, tableResources);
		TextColumn<SubscriptionDTO> planName = new TextColumn<SubscriptionDTO>() {
			@Override
			public String getValue(SubscriptionDTO subscription) {
				return subscription.getSubscriptionPlan().getName();
			}
		};
		Header<String> plaenNameHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "Plan Name";
			}
		};
		table.addColumn(planName, plaenNameHeader);

		TextColumn<SubscriptionDTO> planDescription = new TextColumn<SubscriptionDTO>() {
			@Override
			public String getValue(SubscriptionDTO subscription) {
				return subscription.getSubscriptionPlan().getDescription();
			}
		};
		Header<String> planDescriptionHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "Plan Description";
			}
		};
		table.addColumn(planDescription, planDescriptionHeader);

		TextColumn<SubscriptionDTO> status = new TextColumn<SubscriptionDTO>() {
			@Override
			public String getValue(SubscriptionDTO subscription) {
				return subscription.getStatus().name();
			}
		};
		Header<String> statusHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "Plan Status";
			}
		};
		table.addColumn(status, statusHeader);

		TextColumn<SubscriptionDTO> recurringAmount = new TextColumn<SubscriptionDTO>() {
			@Override
			public String getValue(SubscriptionDTO subscription) {
				return subscription.getSubscriptionPlan().getFee().toString();
			}
		};
		Header<String> recurringAmountHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "Recurring Amount";
			}
		};
		table.addColumn(recurringAmount, recurringAmountHeader);

		TextColumn<SubscriptionDTO> timeCreated = new TextColumn<SubscriptionDTO>() {
			@Override
			public String getValue(SubscriptionDTO subscription) {
				return subscription.getTimeCreated().toString();
			}
		};
		Header<String> timeCreatedHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "Time created";
			}
		};
		table.addColumn(timeCreated, timeCreatedHeader);

		return table;
	}

	@Override
	public void displayTransactionHistory(List<SubscriptionDTO> transactions) {
		transactionDetailsPanel.clear();
		transactionTable = buildTransactionTable();
		this.transactions = transactions;
		transactionTable.addStyleName(style.subscriptionPlanTable());
		transactionTable.setRowData(transactions);
		transactionDetailsPanel.add(transactionTable);

		if (transactions.size() == 0) {
			HTMLPanel panel = new HTMLPanel("<h4>No transactions</h4>");
			panel.setStyleName(style.subscriptionPlanTablePanelMessage());
			transactionDetailsPanel.add(panel);
		}
	}

	@Override
	public void hideTransactionHistory() {
		transactionDetailsPanel.setVisible(false);
	}

	public void showTransactionHistory() {
		transactionDetailsPanel.setVisible(true);
	}

	public void hideSubscriptionTab() {
		subscriptionTab.setHideOn(Device.DESKTOP);
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

	// TODO: NEED TO ADD ABILITY TO ADD LOCATION
	// @UiHandler("addLocationAnchor")
	public void addLocation(ClickEvent event) {
		if (addLocationModal == null) {
			addLocationModal = new AddLocationModal();

			// Add blur handler on zip textbox
			addLocationModal.getZipTextBox().addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (!addLocationModal.validateZip()) {
						return;
					}
					presenter.getNeighborhoodData(FieldVerifier.sanitize(addLocationModal
					    .getZipTextBox()
					    .getText()));
				}
			});

			// Add Location Handler
			addLocationModal.getAddLocationButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					addLocationModal.clearError();
					if (!addLocationModal.validate()) {
						return;
					}

					LocationDTO location = addLocationModal.getLocation();
					account.getLocations().add(location);
					presenter.updateLocation(account);
				}
			});
		}
		addLocationModal.show(true);
	}

	@Override
	public void displayNeighborhoodListLoading(boolean b) {
		addLocationModal.displayNeighborhoodListLoading(b);
	}

	@Override
	public void displayNeighborhoods(List<NeighborhoodDTO> neighbordhoods) {
		addLocationModal.displayNeighborhoods(neighbordhoods);
	}

	@Override
	public void displayMessageInAddLocationWidget(String msg, AlertType type) {
		if (addLocationModal != null) {
			addLocationModal.displayMessage(msg, type);
		}
	}

	@Override
	public void displayLocationModal(boolean display) {
		if (addLocationModal != null) {
			addLocationModal.show(display);
		}
	}

	@Override
	public void showSaveButton(boolean show) {
		saveBtn.setEnabled(show);
	}
}
