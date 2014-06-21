package com.ziplly.app.shared;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.SubscriptionPlanDTO;

public class GetJwtTokenResult implements Result {
	private Map<SubscriptionPlanDTO, String> tokens = new HashMap<SubscriptionPlanDTO, String>();

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
