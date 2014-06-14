package com.ziplly.app.client.view.coupon;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.shared.FieldVerifier;

public class CouponWidget extends Composite {

  private static CouponWidgetUiBinder uiBinder = GWT.create(CouponWidgetUiBinder.class);

  interface CouponWidgetUiBinder extends UiBinder<Widget, CouponWidget> {
  }

  interface CouponTitleTemplate extends SafeHtmlTemplates {
    @Template("${0} voucher for ${1}")
    public SafeHtml couponTitle(String origPrice, String discountedPrice);
  }
  
  public CouponWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    StyleHelper.show(couponDetails, false);
    StyleHelper.show(hideDetailsButton, false);
    setupHandlers();
    ZResources.IMPL.style().ensureInjected();
  }
  
  private void setupHandlers() {
    learnButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        StyleHelper.show(couponDetails, true);
        StyleHelper.show(learnButton, false);
        StyleHelper.show(hideDetailsButton, true);
      }
    });
    
    hideDetailsButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        StyleHelper.show(couponDetails, false);
        StyleHelper.show(learnButton, true);
        StyleHelper.show(hideDetailsButton, false);
      }
      
    });
  }

//  @UiField
//  Heading couponTitle;

  @UiField
  Image image;
  @UiField
  Heading punchLine;
  @UiField
  SpanElement couponDescription;
  @UiField
  DivElement couponDetails;
  @UiField
  SpanElement quantitySold;
  @UiField
  SpanElement quantityRemaining;
  @UiField
  SpanElement expirationShortTime;
  @UiField
  Anchor businessNameAnchor;
  @UiField
  SpanElement businessName;
  @UiField
  SpanElement address;
  @UiField
  SpanElement businessType;
  @UiField
  SpanElement website;
  @UiField
  SpanElement expirationTime;
  @UiField
  SpanElement finePrint;
  @UiField
  Element figure;
  @UiField
  Button learnButton;
  @UiField
  Button hideDetailsButton;
  @UiField
  com.google.gwt.user.client.ui.Button buyButton;
  
  private CouponTitleTemplate titleTemplate = GWT.create(CouponTitleTemplate.class);
  
  private BasicDataFormatter formatter = new BasicDataFormatter();
  
  public void displayCoupon(CouponDTO coupon) {
    ImageDTO imageDto = getImage(coupon);
    if (imageDto != null) {
      image.setUrl(imageDto.getUrl());
    } else {
      StyleHelper.show(figure, false);
    }
    
    setPunchLine(coupon);
//    couponTitle.setText(getTitle(coupon));
    couponDescription.setInnerHTML(SafeHtmlUtils.htmlEscape(coupon.getDescription()));

    BusinessAccountDTO ba = (BusinessAccountDTO) coupon.getTweet().getSender();
    businessName.setInnerText(ba.getDisplayName());
    address.setInnerText(ba.getLocations().get(0).getAddress());
    businessType.setInnerText(ba.getCategory().getName());
    displayWebsiteIfPresent(ba);
    long remaining = coupon.getQuanity() - coupon.getQuantityPurchased();
    quantityRemaining.setInnerText(Long.toString(remaining));
    quantitySold.setInnerText(Long.toString(coupon.getQuantityPurchased()));
    
    expirationTime.setInnerHTML(formatter.format(coupon.getEndDate(), ValueType.DATE_VALUE));
    finePrint.setInnerHTML(ZResources.IMPL.finePrint().getText());
    setExpirationTime(coupon);
  }

  public Anchor getBusinessPageAnchor() {
    return businessNameAnchor;
  }

  private void displayWebsiteIfPresent(BusinessAccountDTO ba) {
    if (!FieldVerifier.isEmpty(ba.getWebsite())) {
      website.setInnerHTML(ba.getWebsite());
    } else {
      website.setInnerHTML(StringConstants.NOT_AVAILABLE);
    }
  }
  
  private void setExpirationTime(CouponDTO coupon) {
    Date now = new Date();
    if (coupon.getEndDate().before(now)) {
      expirationShortTime.setInnerText("EXPIRED");
    } else {
      expirationShortTime.setInnerText(
          BasicDataFormatter.getTimeDiff(coupon.getEndDate(), new Date(), BasicDataFormatter.couponTimeTemplate) + " LEFT");
    }
  }

  public com.google.gwt.user.client.ui.Button getBuyButton() {
    return buyButton;
  }
  
//  private String getTitle(CouponDTO coupon) {
//    String businessName = coupon.getTweet().getSender().getDisplayName();
//    return coupon.getTitle() + " @ " + businessName;
//  }
  
  private void setPunchLine(CouponDTO coupon) {
    SafeHtml text = titleTemplate.couponTitle(coupon.getItemPrice().toString(), coupon.getDiscount().toString());
    punchLine.setText(text.asString());
    
  }

  private ImageDTO getImage(CouponDTO coupon) {
    List<ImageDTO> images = coupon.getTweet().getImages();
    if (images.size() > 0) {
      return images.get(0);
    }
    
    return null;
  }
  
}
