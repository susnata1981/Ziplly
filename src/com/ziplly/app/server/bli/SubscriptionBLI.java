package com.ziplly.app.server.bli;

import java.util.List;

import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public interface SubscriptionBLI {
	SubscriptionEligibilityStatus checkSellerEligibility(Account account);

	String getJwtToken(Long accountId, Long subscriptionId) throws InternalException;

	void completeTransaction(Long accountId, Long subscriptionId) throws NotFoundException;

	List<SubscriptionPlan> getAllSubscriptionPlans();
}
