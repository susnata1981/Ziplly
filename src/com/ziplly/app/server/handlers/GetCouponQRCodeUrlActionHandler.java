package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class GetCouponQRCodeUrlActionHandler extends AbstractAccountActionHandler<GetCouponQRCodeUrlAction, GetCouponQRCodeUrlResult>{
	private CouponTransactionDAO couponTransactionDao;

	@Inject
	public GetCouponQRCodeUrlActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponTransactionDAO couponTransactionDAO) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponTransactionDao = couponTransactionDAO;
  }

	@Override
  public GetCouponQRCodeUrlResult doExecute(GetCouponQRCodeUrlAction action,
      ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.getCouponId());
		
		validateSession();
		List<CouponTransaction> couponTransaction = couponTransactionDao.findCouponTransactionByAccountId(
				session.getAccount().getAccountId());
		
		if (couponTransaction.isEmpty()) {
			throw new AccessError();
		}
		
		return new GetCouponQRCodeUrlResult();
  }

	@Override
  public Class<GetCouponQRCodeUrlAction> getActionType() {
		return GetCouponQRCodeUrlAction.class;
  }
}
