package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.SubscriptionPlanDTO;

public class GetAllSubscriptionPlanResult implements Result {
//	private Map<SubscriptionPlanDTO, String> plans = new HashMap<SubscriptionPlanDTO, String>();

	private List<SubscriptionPlanDTO> subscriptionPlans = new ArrayList<SubscriptionPlanDTO>();
	
	public GetAllSubscriptionPlanResult() {
	}

	public List<SubscriptionPlanDTO> getSubscriptionPlans() {
	  return subscriptionPlans;
  }

	public void setSubscriptionPlans(List<SubscriptionPlanDTO> subscriptions) {
	  this.subscriptionPlans = subscriptions;
  }

//	public Map<SubscriptionPlanDTO, String> getPlans() {
//		return plans;
//	}
//
//	public void setPlans(Map<SubscriptionPlanDTO, String> plans) {
//		this.plans = plans;
//	}
//
//	public void add(SubscriptionPlanDTO plan, String token) {
//		plans.put(plan, token);
//	}
	
}
