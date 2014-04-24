package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class GetCouponTransactionActionHandler extends AbstractAccountActionHandler<GetCouponTransactionAction, GetCouponTransactionResult>{
	private CouponTransactionDAO couponTransactionDao;

	@Inject
	public GetCouponTransactionActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponTransactionDAO couponTransactionDao) {
		
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponTransactionDao = couponTransactionDao;
  }

	@Override
  public Class<GetCouponTransactionAction> getActionType() {
		return GetCouponTransactionAction.class;
  }

	@Override
  public GetCouponTransactionResult doExecute(GetCouponTransactionAction action,
      ExecutionContext context) throws DispatchException {
		
		validateSession();
		List<CouponTransaction> transactions = 
				couponTransactionDao.findCouponTransactionByAccountId(
						session.getAccount().getAccountId(), action.getStart(), action.getPageSize());
		
		Long count = couponTransactionDao.findCouponTransactionCountByAccountId(session.getAccount().getAccountId());

		GetCouponTransactionResult result = new GetCouponTransactionResult();
		for(CouponTransaction transaction : transactions) {
			result.getTransactions().add(EntityUtil.clone(transaction));
		}
		result.setTotalTransactions(count);
		
		return result;
  }
}
