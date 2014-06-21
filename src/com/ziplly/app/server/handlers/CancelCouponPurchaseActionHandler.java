package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.OrderDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.OrderStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Order;
import com.ziplly.app.shared.CancelCouponPurchaseAction;
import com.ziplly.app.shared.CancelCouponPurchaseResult;

public class CancelCouponPurchaseActionHandler extends AbstractAccountActionHandler<CancelCouponPurchaseAction, CancelCouponPurchaseResult>{

  private OrderDAO orderDao;

  @Inject
  public CancelCouponPurchaseActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      OrderDAO orderDao) {
    super(entityManagerProvider, accountDao, sessionDao, accountBli);
    this.orderDao = orderDao;
  }

  @Override
  public Class<CancelCouponPurchaseAction> getActionType() {
    return CancelCouponPurchaseAction.class;
  }

  @Override
  public CancelCouponPurchaseResult doExecute(CancelCouponPurchaseAction action,
      ExecutionContext context) throws DispatchException {
    
    checkArgument(action.getPurchaseCouponId() > 0);
    Order order = orderDao.findById(action.getPurchaseCouponId());
    order.setStatus(OrderStatus.CANCELLED);
    order.getTransaction().setStatus(TransactionStatus.CANCELLED);
    orderDao.save(order);
    return new CancelCouponPurchaseResult();
  }
}
