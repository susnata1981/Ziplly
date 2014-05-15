package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Subscription;

public interface SubscriptionDAO {
	void save(Subscription subscription);
	
	Subscription findById(Long subscriptionId);

	List<Subscription> findByAccountId(Long accountId);
}
