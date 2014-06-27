package com.ziplly.app.client.view.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.PricingPlanWidget;
import com.ziplly.app.client.widget.PricingPlanWidget.TITLE_HUE;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.SubscriptionPlanType;
import com.ziplly.app.model.overlay.SubscriptionDTO;

public class SubscriptionPlansView extends Composite {

  private static SubscriptionPlansViewUiBinder uiBinder = GWT
      .create(SubscriptionPlansViewUiBinder.class);

  interface SubscriptionPlansViewUiBinder extends UiBinder<Widget, SubscriptionPlansView> {
  }

  public SubscriptionPlansView() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiField
  FluidRow row;
  
  private BasicDataFormatter formatter = new BasicDataFormatter();
  
  private Map<Long, PricingPlanWidget> planIdToPlanMap = new HashMap<Long, PricingPlanWidget>();
  
  private Handler handler;
  
  public void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans) {
    int total = plans.size();
    if (total == 0) {
      return;
    }
    
    int colSize = 12 / total; // this should be 3
    
    for(final SubscriptionPlanDTO plan : plans) {
      Column col = new Column(colSize);
      PricingPlanWidget widget = new PricingPlanWidget();
      widget.setTitle(plan.getName());
      widget.setTweetCount(Integer.toString(plan.getTweetsAllowed()));
      widget.setCouponCount(Integer.toString(plan.getCouponsAllowed()));
      widget.setPrice(formatter.format(plan.getFee(), ValueType.PRICE));
      
      if (plan.getPlanType() == SubscriptionPlanType.PREMIUM) {
        widget.setTitleHue(TITLE_HUE.GREEN);
      }
      
      widget.getChoosePlanButton().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          if (handler != null) {
            handler.onSubcriptionSelection(plan);
          }
        }
      });
      
      col.add(widget);
      planIdToPlanMap.put(plan.getSubscriptionId(), widget);
      row.add(col);
    }
  }
  
  public void setSubscriptionPlanHandler(Handler handler) {
    this.handler = handler;
  }
  
  public static interface Handler {
    void onSubcriptionSelection(SubscriptionPlanDTO plan);
  }
  
  public PricingPlanWidget getWidget(long subscriptionPlanId) {
    return planIdToPlanMap.get(subscriptionPlanId);
  }

  public void setActivePlan(SubscriptionDTO activePlan) {
    if (activePlan == null) {
      return;
    }
    
    String buttonText = "Choose";
    boolean basicPlanEnabled = false;
    if (activePlan.getSubscriptionPlan().getPlanType() == SubscriptionPlanType.BASIC) {
      basicPlanEnabled = true;
      buttonText = "Upgrade";
    } 
 
    for(Long subscriptionId : planIdToPlanMap.keySet()) {
      PricingPlanWidget pricingPlanWidget = planIdToPlanMap.get(subscriptionId);
      
      if (subscriptionId == activePlan.getSubscriptionPlan().getSubscriptionId()) {
        pricingPlanWidget.getChoosePlanButton().setText("Active");
        pricingPlanWidget.getChoosePlanButton().setEnabled(false);
      } else {
        pricingPlanWidget.getChoosePlanButton().setText(buttonText);
      }
      
      if (!basicPlanEnabled) {
        pricingPlanWidget.getChoosePlanButton().setEnabled(false);
      }
    }
  }
}
