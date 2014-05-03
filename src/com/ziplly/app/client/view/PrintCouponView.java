package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.ValueType;
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
	Label discount;
	@UiField
	Label startDate;
	@UiField
	Label endDate;
	@UiField
	Label price;
	
	@UiField
	Label termsAndConditionLabel;
	
	public void displayCoupon(GetCouponQRCodeUrlResult result) {
		System.out.println("Seller = "+result.getSeller());
		CouponDTO coupon = result.getCoupon();
		System.out.println("C="+coupon.getTweet());
		logo.setUrl(ZResources.IMPL.zipllyLogo().getSafeUri().asString());
		frame.setSrc(result.getUrl());
		title.setText(coupon.getDescription());
		
		discount.setText(basicDataFormatter.format(coupon.getDiscount(), ValueType.PERCENT));
		price.setText(basicDataFormatter.format(coupon.getPrice(), ValueType.PRICE));
		startDate.setText(basicDataFormatter.format(coupon.getStartDate(), ValueType.DATE_VALUE));
		endDate.setText(basicDataFormatter.format(coupon.getEndDate(), ValueType.DATE_VALUE));
		
		termsAndConditionLabel.setText("You're required to honor all valid coupons that were purchased on Ziplly.");
  }

	public void reset() {
		RootPanel.get("nav").getElement().getStyle().setDisplay(Display.BLOCK);
  }
}
