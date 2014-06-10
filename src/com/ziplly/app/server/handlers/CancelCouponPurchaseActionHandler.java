package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.PurchasedCouponDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.PurchasedCoupon;
import com.ziplly.app.shared.CancelCouponPurchaseAction;
import com.ziplly.app.shared.CancelCouponPurchaseResult;

public class CancelCouponPurchaseActionHandler extends AbstractAccountActionHandler<CancelCouponPurchaseAction, CancelCouponPurchaseResult>{

  private PurchasedCouponDAO purchasedCouponDao;

  @Inject
  public CancelCouponPurchaseActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      PurchasedCouponDAO purchasedCouponDao) {
    super(entityManagerProvider, accountDao, sessionDao, accountBli);
    this.purchasedCouponDao = purchasedCouponDao;
  }

  @Override
  public Class<CancelCouponPurchaseAction> getActionType() {
    return CancelCouponPurchaseAction.class;
  }

  @Override
  public CancelCouponPurchaseResult doExecute(CancelCouponPurchaseAction action,
      ExecutionContext context) throws DispatchException {
    
    checkArgument(action.getPurchaseCouponId() > 0);
    PurchasedCoupon pr = purchasedCouponDao.findById(action.getPurchaseCouponId());
    pr.setStatus(PurchasedCouponStatus.CANCELLED);
    pr.getTransaction().setStatus(TransactionStatus.CANCELLED);
    purchasedCouponDao.save(pr);
    return new CancelCouponPurchaseResult();
  }
}
