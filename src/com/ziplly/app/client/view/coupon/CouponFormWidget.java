package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.Date;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.event.CouponPublishSuccessfulEvent;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.blocks.AbstractBaseTextWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.client.widget.blocks.NumberTextWidget;
import com.ziplly.app.client.widget.blocks.TextAreaWidget;
import com.ziplly.app.client.widget.blocks.TextFieldWidget;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class CouponFormWidget extends AbstractView {

	private static CouponFormWidgetUiBinder uiBinder = GWT.create(CouponFormWidgetUiBinder.class);

	interface CouponFormWidgetUiBinder extends UiBinder<Widget, CouponFormWidget> {
	}

	private AbstractBaseTextWidget titleWidget;
	private AbstractBaseTextWidget descriptionWidget;
	private AbstractBaseTextWidget discountWidget;
	private AbstractBaseTextWidget priceWidget;
	private AbstractBaseTextWidget quantityWidget;
	private AbstractBaseTextWidget totalCouponAllowedWidget;

	@UiField
	ControlGroup titleCg;
	@UiField
	HelpInline titleHelpInline;
	@UiField
	TextBox titleTextBox;
	  
	@UiField
	ControlGroup descriptionCg;
	@UiField
	HelpInline descriptionHelpInline;
	@UiField
	TextArea descriptionTextArea;
	
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
	
//  Upload image
	@UiField
	HTMLPanel uploadProfileImagePanel;
	FormUploadWidget uploadWidget;
	
	@UiField
	Button tweetBtn;
	@UiField
	HorizontalPanel horizontalButtonPanel;
	@UiField
	Button cancelBtn;
	@UiField
	Button previewBtn;
	@UiField
	Anchor changeTweetAnchor;
	@UiField
	HTMLPanel previewPanel;
	@UiField
	HTMLPanel contentPanel;
	@UiField
	Button previewCancelBtn;

	BasicDataFormatter basicDataFormatter = new BasicDataFormatter();
//	private ConfirmationModalWidget confirmationWidget;
	AbstractBaseTextWidget [] textBoxWidgets = new AbstractBaseTextWidget [6];
	
	@Inject
	public CouponFormWidget(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		int count = 0;
		titleWidget = new TextFieldWidget(titleCg, titleTextBox, titleHelpInline);
		textBoxWidgets[count++] = titleWidget;
		
		descriptionWidget = new TextAreaWidget(descriptionCg, descriptionTextArea, descriptionHelpInline);
		textBoxWidgets[count++] = descriptionWidget;
		
		priceWidget = new NumberTextWidget(priceCg, priceTextBox, priceHelpInline);
		textBoxWidgets[count++] = priceWidget;
		
		discountWidget = new NumberTextWidget(discountCg, discountTextBox, discountHelpInline);
		textBoxWidgets[count++] = discountWidget;
		
		quantityWidget = new NumberTextWidget(quantityCg, quantityTextBox, quantityHelpInline);
		textBoxWidgets[count++] = quantityWidget;
		
		totalCouponAllowedWidget = new NumberTextWidget(totalCouponAllowedCg, totalCouponAllowedTextBox, totalCouponAllowedHelpInline);
		textBoxWidgets[count] = totalCouponAllowedWidget;
		
		uploadWidget = new FormUploadWidget(uploadProfileImagePanel);
		
		setupUi();
		setupHandlers();
	}

	private void setupHandlers() {
		eventBus.addHandler(CouponPublishSuccessfulEvent.TYPE, new CouponPublishSuccessfulEvent.Handler() {

			@Override
      public void onEvent(CouponPublishSuccessfulEvent event) {
				clear();
      }
			
		});
  }

	private void setupUi() {
		if (FeatureFlags.OneCouponPerIndividual.isEnabled()) {
			totalCouponAllowedTextBox.setValue("1");
			totalCouponAllowedTextBox.setReadOnly(true);
		}

		showPreview(false);
  }

	public boolean validate() {
		boolean valid = true;
		
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
		coupon.setTitle(titleWidget.getValue());
		coupon.setDescription(descriptionWidget.getValue());
		coupon.setStartDate(startDate.getValue());
		coupon.setEndDate(endDate.getValue());
		coupon.setQuanity(Long.parseLong(quantityWidget.getValue()));
		coupon.setQuantityPurchased(0L);
		BigDecimal discount = BigDecimal.valueOf(Long.parseLong(discountWidget.getValue()));
		coupon.setDiscount(discount);
		BigDecimal itemPrice = BigDecimal.valueOf(Long.parseLong(priceWidget.getValue()));
		coupon.setItemPrice(itemPrice);
		coupon.setNumberAllowerPerIndividual(Integer.parseInt(totalCouponAllowedTextBox.getValue()));
		coupon.setPrice(itemPrice.subtract(itemPrice.multiply(discount).divide(BigDecimal.valueOf(100L))));
		coupon.setTimeCreated(new Date());
		
		if (uploadWidget.hasImage()) {
		  coupon.getTweet().getImages().addAll(uploadWidget.getImage());
		}
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
		
		uploadWidget.reset();
		showPreview(false);
	}
	
	public Button getCancelButton() {
		return cancelBtn;
	}
	
	public Button getPreviewButton() {
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
	
	public FormUploadWidget getFormUploadWidget() {
		return uploadWidget;
	}
	
	public void showPreview(boolean show) {
		if (show) {
			displayPreview();
		}
		StyleHelper.show(previewPanel, show);
	}

	@UiHandler("changeTweetAnchor")
	public void changeTweet(ClickEvent event) {
		showPreview(false);
		showHorizontalButtonPanel(true);
	}

	@UiHandler("previewCancelBtn")
	public void cancelPreview(ClickEvent event) {
		showPreview(false);
		showHorizontalButtonPanel(true);
	}

	public Button getTweetButton() {
		return tweetBtn;
	}
	
	public FormUploadWidget getCouponFormWidget() {
	  return uploadWidget;
	}
	
	private void displayPreview() {
		contentPanel.clear();
		CouponDTO coupon = getCoupon();
		contentPanel.add(new HTMLPanel(basicDataFormatter.format(
		    coupon,
		    ValueType.COUPON)));
//		Button buyNowBtn = new Button("Buy Now");
//		buyNowBtn.setType(ButtonType.PRIMARY);
//		buyNowBtn.getElement().setAttribute("margin", "6px");
//		couponPreviewPanel.add(buyNowBtn);
//		couponFormWidget.showButtons(false);

		if (uploadWidget.hasImage()) {
		  ImageDTO image = coupon.getTweet().getImages().get(0);
		  StyleHelper.setBackgroundImage(contentPanel.getElement(), image.getUrl());
		}
		showHorizontalButtonPanel(false);
		StyleHelper.show(horizontalButtonPanel.getElement(), false);
  }

	private void showHorizontalButtonPanel(boolean show) {
		StyleHelper.show(horizontalButtonPanel, show);
  }
}
