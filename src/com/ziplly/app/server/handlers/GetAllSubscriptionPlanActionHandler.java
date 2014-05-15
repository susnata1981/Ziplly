package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.SubscriptionBLI;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;

public class GetAllSubscriptionPlanActionHandler extends
    AbstractAccountActionHandler<GetAllSubscriptionPlanAction, GetAllSubscriptionPlanResult> {
	private SubscriptionBLI subscriptionBli;

	@Inject
	public GetAllSubscriptionPlanActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    SubscriptionBLI subscriptionBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.subscriptionBli = subscriptionBli;
	}

	@Override
	public GetAllSubscriptionPlanResult doExecute(GetAllSubscriptionPlanAction action,
	    ExecutionContext arg1) throws DispatchException {

		validateSession();
		GetAllSubscriptionPlanResult result = new GetAllSubscriptionPlanResult();
		List<SubscriptionPlan> plans = subscriptionBli.getAllSubscriptionPlans();
		result.setSubscriptionPlans(EntityUtil.cloneSubscriptionPlanList(plans));
		return result;
	}

	@Override
	public Class<GetAllSubscriptionPlanAction> getActionType() {
		return GetAllSubscriptionPlanAction.class;
	}
}
