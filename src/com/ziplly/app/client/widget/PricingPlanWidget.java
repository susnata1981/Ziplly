package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PricingPlanWidget extends Composite {

  private static PricingPlanWidgetUiBinder uiBinder = GWT.create(PricingPlanWidgetUiBinder.class);

  interface PricingPlanWidgetUiBinder extends UiBinder<Widget, PricingPlanWidget> {
  }

  interface Style extends CssResource {
    String blueHue();
    
    String greenHue();
  }
  
  public PricingPlanWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    setTitleHue(TITLE_HUE.BLUE);
  }

  public PricingPlanWidget(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiField
  Style style;
  
  @UiField
  SpanElement titleSpan;
  
  @UiField
  SpanElement priceSpan;

  @UiField
  SpanElement numTweets;

  @UiField
  SpanElement numCoupons;

  @UiField
  DivElement planHeader;
  
  @UiField
  Button choosePlanBtn;
  
  public void setTitle(String title) {
    titleSpan.setInnerText(title);
  }
  
  public void setPrice(String price) {
    priceSpan.setInnerText(price);
  }
  
  public void setTweetCount(String count) {
    numTweets.setInnerText(count);
  }
  
  public void setCouponCount(String count) {
    numCoupons.setInnerText(count);
  }
  
  public void setShowChoosePlanButton(boolean show) {
    StyleHelper.show(choosePlanBtn, show);
  }
  
  public void setTitleHue(TITLE_HUE hue) {
    if (hue == TITLE_HUE.GREEN) {
      planHeader.setClassName(style.greenHue());
    } else {
      planHeader.setClassName(style.blueHue());
    }
  }
 
  public Button getChoosePlanButton() {
    return choosePlanBtn;
  }
  
  public enum TITLE_HUE {
    GREEN,
    BLUE;
  }
}