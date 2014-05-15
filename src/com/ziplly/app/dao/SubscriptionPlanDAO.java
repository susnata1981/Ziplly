package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;

public interface SubscriptionPlanDAO {
	SubscriptionPlan get(Long planId);

	List<SubscriptionPlan> getAll();

	void save(SubscriptionPlan plan);
}
