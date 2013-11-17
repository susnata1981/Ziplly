package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.model.SubscriptionPlanDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetAllSubscriptionPlanResult implements Result {
	private List<SubscriptionPlanDTO> plans = new ArrayList<SubscriptionPlanDTO>();

	public GetAllSubscriptionPlanResult() {
	}
	
	GetAllSubscriptionPlanResult(List<SubscriptionPlanDTO> plans) {
		this.setPlans(plans);
	}

	public List<SubscriptionPlanDTO> getPlans() {
		return plans;
	}

	public void setPlans(List<SubscriptionPlanDTO> plans) {
		this.plans = plans;
	}
}
