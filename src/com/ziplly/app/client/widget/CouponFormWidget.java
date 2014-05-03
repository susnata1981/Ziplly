package com.ziplly.app.client.widget;

import java.math.BigDecimal;
import java.util.Date;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.widget.blocks.DataType;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.client.widget.blocks.TextBoxWidget;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class CouponFormWidget extends Composite {

	private static CouponFormWidgetUiBinder uiBinder = GWT.create(CouponFormWidgetUiBinder.class);

	interface CouponFormWidgetUiBinder extends UiBinder<Widget, CouponFormWidget> {
	}

	private TextBoxWidget couponTextWidget;
	private TextBoxWidget discountWidget;
	private TextBoxWidget priceWidget;
	private TextBoxWidget quantityWidget;
	private TextBoxWidget totalCouponAllowedWidget;

	@UiField
	ControlGroup descriptionCg;
	@UiField
	HelpInline descriptionHelpInline;
	@UiField
	TextBox descriptionTextBox;
	
	@UiField
	ControlGroup startDateCg;
	@UiField
	DateTimeBox startDate;
	@UiField
	HelpInline startDateHelpInline;
	
	@UiField
	ControlGroup endDateCg;
	@UiField
	DateTimeBox endDate;
	@UiField
	HelpInline endDateHelpInline;
	
	@UiField
	ControlGroup priceCg;
	@UiField
	TextBox priceTextBox;
	@UiField
	HelpInline priceHelpInline;
	
	@UiField
	ControlGroup discountCg;
	@UiField
	TextBox discountTextBox;
	@UiField
	HelpInline discountHelpInline;
	
	@UiField
	ControlGroup quantityCg;
	@UiField
	TextBox quantityTextBox;
	@UiField
	HelpInline quantityHelpInline;
	
	@UiField
	ControlGroup totalCouponAllowedCg;
	@UiField
	TextBox totalCouponAllowedTextBox;
	@UiField
	HelpInline totalCouponAllowedHelpInline;
	
	// Upload image
//	@UiField
//	HTMLPanel uploadProfileImagePanel;
	FormUploadWidget uploadWidget;
	
	@UiField
	HorizontalPanel horizontalButtonPanel;
	@UiField
	Button cancelBtn;
	@UiField
	Button previewBtn;

	TextBoxWidget [] textBoxWidgets = new TextBoxWidget [5];
	
	public CouponFormWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		int count = 0;
		couponTextWidget = new TextBoxWidget(descriptionCg, descriptionTextBox, descriptionHelpInline, DataType.STRING);
		textBoxWidgets[count++] = couponTextWidget;
		priceWidget = new TextBoxWidget(priceCg, priceTextBox, priceHelpInline, DataType.INTEGER);
		textBoxWidgets[count++] = priceWidget;
		discountWidget = new TextBoxWidget(discountCg, discountTextBox, discountHelpInline, DataType.INTEGER);
		textBoxWidgets[count++] = discountWidget;
		quantityWidget = new TextBoxWidget(quantityCg, quantityTextBox, quantityHelpInline, DataType.INTEGER);
		textBoxWidgets[count++] = quantityWidget;
		totalCouponAllowedWidget = new TextBoxWidget(totalCouponAllowedCg, totalCouponAllowedTextBox, totalCouponAllowedHelpInline, DataType.INTEGER);
		textBoxWidgets[count] = totalCouponAllowedWidget;
		
//		uploadWidget = new FormUploadWidget(uploadProfileImagePanel);
		
		setupUi();
	}

	private void setupUi() {
		if (FeatureFlags.OneCouponPerIndividual.isEnabled()) {
			totalCouponAllowedTextBox.setValue("1");
			totalCouponAllowedTextBox.setReadOnly(true);
		}
  }

	boolean validate() {
		boolean valid = couponTextWidget.validate();
		for(int i=0; i<textBoxWidgets.length; i++) {
			valid &= textBoxWidgets[i].validate();
		}
		
		ValidationResult result = FieldVerifier.validateStartDate(startDate.getValue());
		if (!result.isValid()) {
			setError(startDateCg, startDateHelpInline, result.getErrors().get(0).getErrorMessage());
		}
		valid &= result.isValid();
		
		result = FieldVerifier.validateEndDate(endDate.getValue(), startDate.getValue());
		if (!result.isValid()) {
			setError(endDateCg, endDateHelpInline, result.getErrors().get(0).getErrorMessage());
		}
		
		valid &= result.isValid();		
		return valid;
	}
	
	public CouponDTO getCoupon() {
		resetForm();
		if (!validate()) {
			return null;
		}
		
		CouponDTO coupon = new CouponDTO();
		coupon.setDescription(FieldVerifier.sanitize(descriptionTextBox.getText()));
		coupon.setStartDate(startDate.getValue());
		coupon.setEndDate(endDate.getValue());
		coupon.setQuanity(Long.parseLong(FieldVerifier.sanitize(quantityTextBox.getText())));
		coupon.setQuantityPurchased(0L);
		BigDecimal discount = BigDecimal.valueOf(Long.parseLong(FieldVerifier.sanitize(discountTextBox.getText())));
		coupon.setDiscount(discount);
		BigDecimal itemPrice = BigDecimal.valueOf(Long.parseLong(FieldVerifier.sanitize(priceTextBox.getText())));
		coupon.setItemPrice(itemPrice);
		coupon.setNumberAllowerPerIndividual(Integer.parseInt(totalCouponAllowedTextBox.getValue()));
		coupon.setPrice(itemPrice.subtract(itemPrice.multiply(discount).divide(BigDecimal.valueOf(100L))));
		coupon.setTimeCreated(new Date());
		return coupon;
	}
	
	public void resetForm() {
		for(int i=0; i<textBoxWidgets.length; i++) {
			textBoxWidgets[i].resetError();
		}
		
		startDateCg.setType(ControlGroupType.NONE);
		startDateHelpInline.setText("");
		startDateHelpInline.setVisible(false);
		
		endDateCg.setType(ControlGroupType.NONE);
		endDateHelpInline.setText("");
		endDateHelpInline.setVisible(false);
	}
	
	public void clear() {
		for(int i=0; i<textBoxWidgets.length; i++) {
			if (FeatureFlags.OneCouponPerIndividual.isEnabled()) {
				if (textBoxWidgets[i] == totalCouponAllowedWidget) {
					continue;
				}
			}
			textBoxWidgets[i].clear();
		}
		
		startDateCg.setType(ControlGroupType.NONE);
		startDateHelpInline.setText("");
		startDateHelpInline.setVisible(false);
		
		endDateCg.setType(ControlGroupType.NONE);
		endDateHelpInline.setText("");
		endDateHelpInline.setVisible(false);
	}
	
	Button getCancelButton() {
		return cancelBtn;
	}
	
	Button getPreviewButton() {
		return previewBtn;
	}
	
	private void setError(ControlGroup cg, HelpInline helpInline, String error) {
		cg.setType(ControlGroupType.ERROR);
		helpInline.setText(error);
		helpInline.setVisible(true);
	}

	public void showButtons(boolean display) {
		StyleHelper.show(horizontalButtonPanel.getElement(), display);
  }

	public void setFormUploadActionUrl(String url) {
		uploadWidget.setUploadFormActionUrl(url);
  }
	
//
//	@Deprecated
//	public void displayCouponImagePreview(String imageUrl) {
//		uploadWidget.displayImagePreview(imageUrl);
//  }
	
	public FormUploadWidget getFormUploadWidget() {
		return uploadWidget;
	}
}
