package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SubscriptionPlanDAO;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;

public class GetAllSubscriptionPlanActionHandler extends AbstractAccountActionHandler<GetAllSubscriptionPlanAction, GetAllSubscriptionPlanResult>{

	private SubscriptionPlanDAO subscriptionPlanDao;

	@Inject
	public GetAllSubscriptionPlanActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli, SubscriptionPlanDAO subscriptionPlanDao) {
		super(accountDao, sessionDao, accountBli);
		this.subscriptionPlanDao = subscriptionPlanDao;
	}

	@Override
	public GetAllSubscriptionPlanResult execute(
			GetAllSubscriptionPlanAction action, ExecutionContext arg1)
			throws DispatchException {
		GetAllSubscriptionPlanResult result = new GetAllSubscriptionPlanResult();
		List<SubscriptionPlan> plans = subscriptionPlanDao.getAll();
		for(SubscriptionPlan plan : plans) {
			result.getPlans().add(EntityUtil.clone(plan));
		}
		return result;
	}

	@Override
	public Class<GetAllSubscriptionPlanAction> getActionType() {
		return GetAllSubscriptionPlanAction.class;
	}

}
