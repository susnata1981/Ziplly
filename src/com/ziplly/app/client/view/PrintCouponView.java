package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class PrintCouponView extends AbstractView {

	private static PrintCouponViewUiBinder uiBinder = GWT.create(PrintCouponViewUiBinder.class);

	interface PrintCouponViewUiBinder extends UiBinder<Widget, PrintCouponView> {
	}

	@Inject
	public PrintCouponView(EventBus eventBus) {
		super(eventBus);
		RootPanel.get("nav").getElement().getStyle().setDisplay(Display.NONE);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void displayMessage(String errorMessage, AlertType type) {
	  
  }

	@UiField
	Image logo;
	@UiField
	IFrameElement frame;
	@UiField
	Heading title;

	@UiField
	Label businessName;
	@UiField
	Label businessAddress;
	@UiField
	Label businessPhone;
	@UiField
	Label discount;
	@UiField
	Label startDate;
	@UiField
	Label endDate;
	@UiField
	Label price;
	@UiField
	Label totalCouponsAllowerPerPerson;
	
	@UiField
	SpanElement termsAndConditionLabel;
	
	private String finePrint = "One coupon per customer per day. Coupon must be surrendered at the time of purchase. "
	    + "May not be used for prior "
	    + "purchases or sale price items or combined with any other coupon, offer , sale or discount.";
	
	public void displayCoupon(GetCouponQRCodeUrlResult result) {
		CouponDTO coupon = result.getCoupon();
		logo.setUrl(ZResources.IMPL.zipllyLogo().getSafeUri().asString());
		frame.setSrc(result.getUrl());
		title.setText(coupon.getDescription());
		
		BusinessAccountDTO businessAccount = (BusinessAccountDTO) result.getSeller();
		businessName.setText(businessAccount.getDisplayName());
		businessAddress.setText(businessAccount.getLocations().get(0).getAddress());
		businessPhone.setText(businessAccount.getPhone());
		
		discount.setText(basicDataFormatter.format(coupon.getDiscount(), ValueType.PERCENT));
		price.setText(basicDataFormatter.format(coupon.getPrice(), ValueType.PRICE));
		startDate.setText(basicDataFormatter.format(coupon.getStartDate(), ValueType.DATE_VALUE));
		endDate.setText(basicDataFormatter.format(coupon.getEndDate(), ValueType.DATE_VALUE));
		
		totalCouponsAllowerPerPerson.setText(Integer.toString(coupon.getNumberAllowerPerIndividual()));
		termsAndConditionLabel.setInnerText(finePrint);
  }

	public void reset() {
		RootPanel.get("nav").getElement().getStyle().setDisplay(Display.BLOCK);
  }
}
