package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.SubscriptionBLI;
import com.ziplly.app.server.model.jpa.BusinessAccount;
import com.ziplly.app.shared.CheckSubscriptionEligibilityAction;
import com.ziplly.app.shared.CheckSubscriptionEligibilityResult;
import com.ziplly.app.shared.SubscriptionEligibilityStatus;

public class CheckSubscriptionEligibilityActionHandler
    extends
    AbstractAccountActionHandler<CheckSubscriptionEligibilityAction, CheckSubscriptionEligibilityResult> {
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

    BusinessAccount baccount = BusinessAccount.class.cast(session.getAccount());
    SubscriptionEligibilityStatus status = subscriptionBli.checkSellerEligibility(baccount);
    CheckSubscriptionEligibilityResult result = new CheckSubscriptionEligibilityResult();
    result.setEligibilityStatus(status);
    
    if (status == SubscriptionEligibilityStatus.ELIGIBLE) {
      String jwtToken =
          subscriptionBli.getJwtToken(
              session.getAccount().getAccountId(),
              action.getSubscriptionId());
      result.setToken(jwtToken);
    }
    return result;
  }

  @Override
  public Class<CheckSubscriptionEligibilityAction> getActionType() {
    return CheckSubscriptionEligibilityAction.class;
  }
}
