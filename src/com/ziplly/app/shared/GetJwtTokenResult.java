package com.ziplly.app.shared;

import java.util.Map;

import com.google.common.collect.Maps;
import com.ziplly.app.model.SubscriptionPlanDTO;

import net.customware.gwt.dispatch.shared.Result;

public class GetJwtTokenResult implements Result {
	private Map<SubscriptionPlanDTO, String> tokens = Maps.newHashMap();

	public GetJwtTokenResult() {
	}
	
	public Map<SubscriptionPlanDTO, String> getTokens() {
		return tokens;
	}

	public void setTokens(Map<SubscriptionPlanDTO, String> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(SubscriptionPlanDTO plan, String token) {
		tokens.put(plan, token);
	}
}
