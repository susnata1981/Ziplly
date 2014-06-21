package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.Date;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.event.CouponPublishSuccessfulEvent;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.blocks.AbstractBaseTextWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.client.widget.blocks.PositiveNumberTextWidget;
import com.ziplly.app.client.widget.blocks.TextAreaWidget;
import com.ziplly.app.client.widget.blocks.TextFieldWidget;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.FeatureFlags;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class CouponFormWidget extends AbstractView {
  public static final DateTimeFormat PST_FORMAT = DateTimeFormat
      .getFormat("dd/mm/yyyy hh:mm zz");
	private static final int DESCRIPTION_MAX_LENGTH = 500;
  private static final int DESCRIPTION_MIN_LENGTH = 30;
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
	Alert message;
	
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
	Panel horizontalButtonPanel;
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
	AbstractBaseTextWidget [] textBoxWidgets = new AbstractBaseTextWidget [6];
  private BusinessAccountDTO account;
	
	public CouponFormWidget(EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		int count = 0;
		titleWidget = new TextFieldWidget(titleCg, titleTextBox, titleHelpInline);
		textBoxWidgets[count++] = titleWidget;
		
		descriptionWidget = new TextAreaWidget(descriptionCg, 
		    descriptionTextArea, descriptionHelpInline, DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH);
		
		textBoxWidgets[count++] = descriptionWidget;
		
		priceWidget = new PositiveNumberTextWidget(priceCg, priceTextBox, priceHelpInline);
		textBoxWidgets[count++] = priceWidget;
		
		discountWidget = new PositiveNumberTextWidget(discountCg, discountTextBox, discountHelpInline);
		textBoxWidgets[count++] = discountWidget;
		
		quantityWidget = new PositiveNumberTextWidget(quantityCg, quantityTextBox, quantityHelpInline);
		textBoxWidgets[count++] = quantityWidget;
		
		totalCouponAllowedWidget = new PositiveNumberTextWidget(totalCouponAllowedCg, totalCouponAllowedTextBox, totalCouponAllowedHelpInline);
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
		
		startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {

      @Override
      public void onValueChange(ValueChangeEvent<Date> event) {
        ValidationResult result = FieldVerifier.validateStartDate(startDate.getValue());
        startDate.getValue().getTime();
        if (!result.isValid()) {
          setError(startDateCg, startDateHelpInline, result.getErrors().get(0).getErrorMessage());
        } else {
          setStatus(ControlGroupType.SUCCESS, startDateCg, startDateHelpInline, "");
        }
      }
		  
		});
		
		endDate.addValueChangeHandler(new ValueChangeHandler<Date>() {

      @Override
      public void onValueChange(ValueChangeEvent<Date> event) {
        ValidationResult result = FieldVerifier.validateEndDate(endDate.getValue(), startDate.getValue());
        if (!result.isValid()) {
          setError(endDateCg, endDateHelpInline, result.getErrors().get(0).getErrorMessage());
        } else {
          setStatus(ControlGroupType.SUCCESS, endDateCg, endDateHelpInline, "");
        }
      }
		  
		});
	}

	private void setupUi() {
		if (FeatureFlags.OneCouponPerIndividual.isEnabled()) {
			totalCouponAllowedTextBox.setValue("1");
			totalCouponAllowedTextBox.setReadOnly(true);
		}

		message.setVisible(false);
		displayPreview(false);
  }

	public boolean validate() {
	  resetForm();
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
		
		if (valid) {
		  valid = validateDiscount();
		}
		
		return valid;
	}
	
	private boolean validateDiscount() {
	  Double price = Double.parseDouble(FieldVerifier.sanitize(priceWidget.getValue()));
	  Double discountedPrice = Double.parseDouble(FieldVerifier.sanitize(discountWidget.getValue()));
	  
	  if (discountedPrice >= price) {
	    String msg = "Discount can't be more than original price ["+price+"]";
	    setStatus(ControlGroupType.ERROR, discountCg, discountHelpInline, msg);
	    return false;
	  }
	  
	  double discount = (100 - 100 * (discountedPrice/price));
	  if (discount > 50) {
	    String msg = "Are you sure you want to offer the voucher for "+discount+"% off";
	    boolean confirm = Window.confirm(msg);
	    return confirm;
	  }
	  
	  return true;
  }

  public CouponDTO getCoupon() {
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
		BigDecimal discount = BigDecimal.valueOf(Double.parseDouble(discountWidget.getValue()));
		BigDecimal itemPrice = BigDecimal.valueOf(Double.parseDouble(priceWidget.getValue()));
		coupon.setItemPrice(itemPrice);
		coupon.setDiscountedPrice(discount);
		coupon.setNumberAllowerPerIndividual(Integer.parseInt(totalCouponAllowedTextBox.getValue()));
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
		displayPreview(false);
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
		StyleHelper.show(horizontalButtonPanel, display);
  }

	public void setFormUploadActionUrl(String url) {
		uploadWidget.setUploadFormActionUrl(url);
  }
	
	public FormUploadWidget getFormUploadWidget() {
		return uploadWidget;
	}
	
	private void displayPreview(boolean show) {
		StyleHelper.show(previewPanel, show);
	}

	@UiHandler("changeTweetAnchor")
	public void changeTweet(ClickEvent event) {
		displayPreview(false);
		showHorizontalButtonPanel(true);
	}

	@UiHandler("previewCancelBtn")
	public void cancelPreview(ClickEvent event) {
		displayPreview(false);
		showHorizontalButtonPanel(true);
	}

	public Button getTweetButton() {
		return tweetBtn;
	}
	
	public FormUploadWidget getCouponFormWidget() {
	  return uploadWidget;
	}
	
	public void showPreview() {
	  message.setVisible(false);
		contentPanel.clear();
		CouponDTO coupon = getCoupon();
		if (coupon == null) {
		  AlertModal modal = new AlertModal();
		  modal.setTitle(stringDefinitions.failedValidation());
		  return;
		}
		
		TweetDTO tweet = new TweetDTO();
		tweet.setSender(account);
		coupon.setTweet(tweet);
		CouponWidget couponWidget = new CouponWidget();
		couponWidget.displayCoupon(coupon);
		contentPanel.add(couponWidget);
		if (uploadWidget.hasImage()) {
		  ImageDTO image = coupon.getTweet().getImages().get(0);
		  StyleHelper.setBackgroundImage(contentPanel.getElement(), image.getUrl());
		}
		showHorizontalButtonPanel(false);
		displayPreview(true);
  }

	private void showHorizontalButtonPanel(boolean show) {
		StyleHelper.show(horizontalButtonPanel, show);
  }

  public BusinessAccountDTO getAccount() {
    return account;
  }

  public void setAccount(BusinessAccountDTO account) {
    this.account = account;
  }
  
  public void displayInlineMessage(String msg, AlertType type) {
    message.setText(msg);
    message.setType(type);
    message.setVisible(true);
  }
  
  private void setStatus(ControlGroupType type, ControlGroup cg, HelpInline helpInline, String msg) {
    cg.setType(type);
    helpInline.setText(msg);
    helpInline.setVisible(false);
  }
}
