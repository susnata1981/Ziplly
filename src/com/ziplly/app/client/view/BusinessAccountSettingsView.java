package com.ziplly.app.client.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.Device;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.AccountSettingsPresenter;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity.IBusinessAccountSettingView;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class BusinessAccountSettingsView extends Composite implements IBusinessAccountSettingView {

	private static final Double MEMBERSHIP_FEE_AMOUNT = 5.0;
	private Logger logger = Logger.getLogger(BusinessAccountSettingsView.class.getName());
	private static BusinessAccountSettingsViewUiBinder uiBinder = GWT
			.create(BusinessAccountSettingsViewUiBinder.class);

	public static interface BusinessAccountSettingsPresenter extends AccountSettingsPresenter<BusinessAccountDTO> {
		void getJwtString();

		void pay(TransactionDTO txn);
	}
	
	interface BusinessAccountSettingsViewUiBinder extends
			UiBinder<Widget, BusinessAccountSettingsView> {
	}

	@UiField
	Alert message;
	
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
	TextBox street2;
	@UiField
	ControlGroup street2Cg;
	@UiField
	HelpInline street2Error;
	
	@UiField
	TextBox zip;
	@UiField
	ControlGroup zipCg;
	@UiField
	HelpInline zipError;

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
	FormPanel uploadForm;
	@UiField
	Button uploadBtn;
	@UiField
	Image profileImagePreview;
	
	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;
	
	// Payment details tab
	@UiField
	Alert paymentStatus;
	@UiField
	HTMLPanel subscriptionDetailsTable;
	@UiField
	HTMLPanel subscriptionPlanTablePanel;
	
	// transaction details elements
	@UiField
	Tab subscriptionTab;
	@UiField
	TableCellElement subscriptionPlanName;
	@UiField
	TableCellElement subscriptionPlanDescription;
	@UiField
	TableCellElement subscriptionPlanFee;
	@UiField
	TableCellElement subscriptionPlanStatus;
	
	private CellTable<SubscriptionPlanDTO> subscriptionPlanTable;	
	private BusinessAccountDTO account;

	private BusinessAccountSettingsPresenter presenter;

	private String jwtToken;
	private Map<Long,SubscriptionPlanDTO> subscriptionPlanMap = new HashMap<Long, SubscriptionPlanDTO>();
	private boolean actionEnabled = true;
	
	public BusinessAccountSettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		clearPaymentStatus();
	}
	
	@Override
	public void clear() {
		this.account = null;
		message.clear();
		message.setVisible(false);
		businessName.setText("");
		street1.setText("");
		street2.setText("");
		zip.setText("");
		website.setText("");
		email.setText("");
		clearPaymentStatus();
	}

	@Override
	public void displaySettings(BusinessAccountDTO account) {
		resetUploadForm();
		if (account != null) {
			this.account = account;
			businessName.setText(account.getName());
			street1.setText(account.getStreet1());
			street2.setText(account.getStreet2());
			zip.setText(Integer.toString(account.getZip()));
			website.setText(account.getWebsite());
			email.setText(account.getEmail());
			if (account.getImageUrl() != null) {
				displayImagePreview(account.getImageUrl());
			}

			// Don't show subscription tab for non-profits
			if (account.getBusinessType() == BusinessType.NON_PROFIT) {
				hideSubscriptionTab();
			}
		}
	}

	boolean validateZip() {
		String zipInput = zip.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			zipCg.setType(ControlGroupType.ERROR);
			zipError.setText(validateZip.getErrors().get(0).getErrorMessage());
			zipError.setVisible(true);
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

	boolean validatePassword(String password, ControlGroup cg,
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
		String businessNameInput = businessName.getText().trim();
		boolean valid = true;
		valid &= validateName(businessNameInput, businessNameCg, businessNameError);

		String lastnameInput = street1.getText().trim();
		valid &= validateName(lastnameInput, street1Cg, street1Error);

		valid &= validateEmail();

		valid &= validateZip();

		return valid;
	}
	
	void resetErrors() {
		businessNameCg.setType(ControlGroupType.NONE);
		businessNameError.setVisible(false);
		street1Cg.setType(ControlGroupType.NONE);
		street1Error.setVisible(false);
		zipCg.setType(ControlGroupType.NONE);
		zipError.setVisible(false);
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
		String streetOne = street1.getText().trim();
		String streetTwo = street2.getText().trim();
		String websiteUrl = website.getText().trim();
		String emailInput = email.getText().trim();
		String zipInput = zip.getText().trim();
		String imageUrl = profileImagePreview.getUrl();
		BusinessAccountDTO acct = new BusinessAccountDTO();
		acct.setAccountId(account.getAccountId());
		acct.setUid(account.getUid());
		acct.setName(name);
		acct.setStreet1(streetOne);
		acct.setStreet2(streetTwo);
		acct.setZip(Integer.parseInt(zipInput));
		acct.setWebsite(websiteUrl);
		acct.setEmail(emailInput);
		if (imageUrl != null) {
			acct.setImageUrl(imageUrl);
		}
		presenter.save(acct);
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
	public void clearError() {
		message.setVisible(false);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setType(type);
		message.setText(msg);
		message.setVisible(true);
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
	
	native void doPay(String jwtToken, int subscriptionPlanId) /*-{
		var that = this;
		$wnd.google.payments.inapp.buy({
			'jwt': jwtToken,
			'success' : function() { 
				alert('success');
				that.@com.ziplly.app.client.view.BusinessAccountSettingsView::onSuccess(I)(subscriptionPlanId);
			},
			'failure' : function() { 
				alert('failure'); 
				that.@com.ziplly.app.client.view.BusinessAccountSettingsView::onFailure()();
			}
		});
	}-*/;
	
	public void onSuccess(int subscriptionId) {
		logger.log(Level.INFO, "Successfully paid");
		TransactionDTO txn = new TransactionDTO();
		txn.setCurrencyCode(StringConstants.CURRENCY_CODE);
		txn.setAmount(new BigDecimal(MEMBERSHIP_FEE_AMOUNT));
		txn.setTimeCreated(new Date());
		txn.setStatus(TransactionStatus.ACTIVE);
		txn.setStatus(TransactionStatus.FAILURE);
		txn.setPlan(subscriptionPlanMap.get(new Long(subscriptionId)));
		presenter.pay(txn);
	};

	public void onFailure() {
		logger.log(Level.SEVERE, "Transaction failed.");
		TransactionDTO txn = new TransactionDTO();
		txn.setCurrencyCode(StringConstants.CURRENCY_CODE);
		txn.setAmount(new BigDecimal(MEMBERSHIP_FEE_AMOUNT));
		txn.setTimeCreated(new Date());
		txn.setStatus(TransactionStatus.FAILURE);		
		presenter.pay(txn);
	};

	@Override
	public void setPresenter(BusinessAccountSettingsPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setJwtString(String jwt) {
		this.jwtToken = jwt;
	}

	@Override
	public void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans) {
		subscriptionPlanMap.clear();
		for(SubscriptionPlanDTO plan : plans) {
			subscriptionPlanMap.put(plan.getSubscriptionId(), plan);
		}
		subscriptionPlanTablePanel.clear();
		clearPaymentStatus();
		subscriptionPlanTable = buildSubscriptionTable();
		subscriptionPlanTable.setRowData(plans);
		subscriptionPlanTablePanel.add(subscriptionPlanTable);
	}

	private CellTable<SubscriptionPlanDTO> buildSubscriptionTable() {
		CellTable<SubscriptionPlanDTO> table = new CellTable<SubscriptionPlanDTO>();
		TextColumn<SubscriptionPlanDTO> name = new TextColumn<SubscriptionPlanDTO>() {
			@Override
			public String getValue(SubscriptionPlanDTO plan) {
				return plan.getName();
			}
		};
		Header<String> nameHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "name";
			}
		};
		table.addColumn(name, nameHeader);
		
		TextColumn<SubscriptionPlanDTO> description = new TextColumn<SubscriptionPlanDTO>() {
			@Override
			public String getValue(SubscriptionPlanDTO plan) {
				return plan.getDescription();
			}
		};
		Header<String> descriptionHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "description";
			}
		};
		table.addColumn(description, descriptionHeader);
		
		TextColumn<SubscriptionPlanDTO> fee = new TextColumn<SubscriptionPlanDTO>() {
			@Override
			public String getValue(SubscriptionPlanDTO plan) {
				return plan.getFee().intValue() + "$";
			}
		};
		Header<String> feeHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "fee";
			}
		};
		table.addColumn(fee, feeHeader);
		
		ButtonCell buttonCell = new ButtonCell();
		Column<SubscriptionPlanDTO, String> actionColumn = new Column<SubscriptionPlanDTO, String>(buttonCell) {
			@Override
			public String getValue(SubscriptionPlanDTO object) {
				return "subscribe";
			}
		};
		Header<String> actionHeader = new Header<String>(new TextCell()) {
			@Override
			public String getValue() {
				return "action";
			}
		};
		table.addColumn(actionColumn, actionHeader);
		
		actionColumn.setFieldUpdater(new FieldUpdater<SubscriptionPlanDTO, String>() {

			@Override
			public void update(int index, SubscriptionPlanDTO object, String value) {
				if (actionEnabled) {
					doPay(jwtToken, object.getSubscriptionId().intValue());
				} else {
					Window.alert("You're already subscribed!");
				}
			}
		});
		return table;
	}

	@Override
	public void displayTransactionHistory(TransactionDTO txn) {
		if (txn != null) {
			subscriptionPlanName.setInnerHTML(txn.getPlan().getName());
			subscriptionPlanDescription.setInnerHTML(txn.getPlan().getDescription());
			subscriptionPlanFee.setInnerHTML(txn.getPlan().getFee().toString());
			subscriptionPlanStatus.setInnerHTML(StringConstants.TRANSACTION_ACTIVE);
			showTransactionHistory();
		}
	}

	@Override
	public void disableSubscriptionButton() {
		actionEnabled = false;
	}

	@Override
	public void hideTransactionHistory() {
		subscriptionDetailsTable.setVisible(false);
	}
	
	public void showTransactionHistory() {
		subscriptionDetailsTable.setVisible(true);
	}
	
	public void hideSubscriptionTab() {
		subscriptionTab.setHideOn(Device.DESKTOP);
	}
}
