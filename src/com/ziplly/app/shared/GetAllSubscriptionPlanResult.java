package com.ziplly.app.shared;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.SubscriptionPlanDTO;

public class GetAllSubscriptionPlanResult implements Result {
	private Map<SubscriptionPlanDTO, String> plans = new HashMap<SubscriptionPlanDTO, String>();

	public GetAllSubscriptionPlanResult() {
	}

	public Map<SubscriptionPlanDTO, String> getPlans() {
		return plans;
	}

	public void setPlans(Map<SubscriptionPlanDTO, String> plans) {
		this.plans = plans;
	}

	public void add(SubscriptionPlanDTO plan, String token) {
		plans.put(plan, token);
	}
}
