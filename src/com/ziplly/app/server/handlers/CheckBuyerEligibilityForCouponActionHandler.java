package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;

public class CheckBuyerEligibilityForCouponActionHandler 
	extends AbstractAccountActionHandler<CheckBuyerEligibilityForCouponAction, CheckBuyerEligibilityForCouponResult>{

	@Inject
	public CheckBuyerEligibilityForCouponActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
  }

	@Override
  public CheckBuyerEligibilityForCouponResult
      doExecute(CheckBuyerEligibilityForCouponAction action, ExecutionContext context) throws DispatchException {
		
		checkArgument(action.getCoupon() != null);
		//TODO(shaan): Create a pending transaction here, so in case there's an error after
		// the user pays, we can resolve it.
		return new CheckBuyerEligibilityForCouponResult();
  }

	@Override
  public Class<CheckBuyerEligibilityForCouponAction> getActionType() {
		return CheckBuyerEligibilityForCouponAction.class;
  }
}
