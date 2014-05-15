package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import static com.google.common.base.Preconditions.checkNotNull;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.SubscriptionBLI;
import com.ziplly.app.shared.CheckSubscriptionEligibilityAction;
import com.ziplly.app.shared.CheckSubscriptionEligibilityResult;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public class CheckSubscriptionEligibilityActionHandler extends AbstractAccountActionHandler<CheckSubscriptionEligibilityAction, CheckSubscriptionEligibilityResult>{
	private SubscriptionBLI subscriptionBli;
	
	@Inject
	public CheckSubscriptionEligibilityActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      SubscriptionBLI subscriptionBli) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.subscriptionBli = subscriptionBli;
  }

	@Override
  public CheckSubscriptionEligibilityResult doExecute(CheckSubscriptionEligibilityAction action,
      ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.getSubscriptionId());
		validateSession();
		
		SubscriptionEligibilityStatus status = subscriptionBli.checkSellerEligibility(session.getAccount());
		CheckSubscriptionEligibilityResult result = new CheckSubscriptionEligibilityResult();
		String jwtToken = subscriptionBli.getJwtToken(session.getAccount().getAccountId(), action.getSubscriptionId());
		result.setEligibilityStatus(status);
		result.setToken(jwtToken);
		return result;
  }

	@Override
  public Class<CheckSubscriptionEligibilityAction> getActionType() {
		return CheckSubscriptionEligibilityAction.class;
  }
}
