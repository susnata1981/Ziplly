package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;

public interface SubscriptionPlanDAO {
	SubscriptionPlan get(Long planId);

	List<SubscriptionPlanDTO> getAll();

	void save(SubscriptionPlan plan);
}
