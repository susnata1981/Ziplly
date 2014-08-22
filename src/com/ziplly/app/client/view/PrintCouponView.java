package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
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

	@UiField
	Image logo;
	@UiField
	IFrameElement frame;
	@UiField
	SpanElement title;

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
	SpanElement price;
	@UiField
	Label totalCouponsAllowerPerPerson;
	
	@UiField
	SpanElement termsAndConditionLabel;
	
	private String finePrint = "One coupon per customer per day. Coupon must be surrendered at the time of purchase. "
	    + "May not be used for prior "
	    + "purchases or sale price items or combined with any other coupon, offer , sale or discount. The coupon"
	    + "can only be used during the promotion start and end date. After the expiration date, coupon can't be"
	    + "used for redemption.";
	
	public void displayCoupon(GetCouponQRCodeUrlResult result) {
		CouponDTO coupon = result.getCoupon();
		logo.setUrl(ZResources.IMPL.zipllyLogo().getSafeUri().asString());
		frame.setSrc(result.getUrl());
		title.setInnerText(coupon.getDescription());
		
		BusinessAccountDTO businessAccount = (BusinessAccountDTO) result.getSeller();
		businessName.setText(businessAccount.getDisplayName());
		businessAddress.setText(businessAccount.getLocations().get(0).getAddress());
		businessPhone.setText(businessAccount.getPhone());
		
		discount.setText(basicDataFormatter.format(coupon.getDiscountedPrice(), ValueType.PRICE));
		price.setInnerText(basicDataFormatter.format(coupon.getItemPrice(), ValueType.PRICE));
		startDate.setText(basicDataFormatter.format(coupon.getStartDate(), ValueType.DATE_VALUE_FULL));
		endDate.setText(basicDataFormatter.format(coupon.getEndDate(), ValueType.DATE_VALUE_FULL));
		
		totalCouponsAllowerPerPerson.setText(Integer.toString(coupon.getNumberAllowerPerIndividual()));
		termsAndConditionLabel.setInnerText(finePrint);
  }

	public void reset() {
		RootPanel.get("nav").getElement().getStyle().setDisplay(Display.BLOCK);
  }
}
