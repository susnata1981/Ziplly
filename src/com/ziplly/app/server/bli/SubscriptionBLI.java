package com.ziplly.app.server.bli;

import java.util.List;

import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.SubscriptionPlan;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public interface SubscriptionBLI {
	SubscriptionEligibilityStatus checkSellerEligibility(Account account);

	String getJwtToken(Long accountId, Long subscriptionId) throws InternalException;

	List<SubscriptionPlan> getAllSubscriptionPlans();

  void completeTransaction(Long accountId, Long subscriptionId, String orderId) throws NotFoundException;

  void cancelOrder(String orderId);
}
