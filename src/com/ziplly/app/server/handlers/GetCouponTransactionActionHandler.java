package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
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
import com.ziplly.app.model.TransactionStatus;
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
		TransactionDetails details = null;
		switch(action.getSearchType()) {
			case BY_ACCOUNT_ID:
				details = searchByAccountId(action);
				break;
			case BY_COUPON_ID:
				checkNotNull(action.getCouponId());
				checkArgument(action.getStart() >= 0);
				checkArgument(action.getPageSize() > 0);
				details = searchByCouponId(action);
		}
		 
		GetCouponTransactionResult result = new GetCouponTransactionResult();
		result.setTransactions(EntityUtil.cloneCouponTransactionList(details.getTransactions()));
		result.setTotalTransactions(details.getTotalTransactionCount());
		return result;
  }

	private TransactionDetails searchByCouponId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setTransactions(couponTransactionDao.findCouponTransactionByCouponId(action.getCouponId(), action.getStart(), action.getPageSize()));
		details.setTotalTransactionCount(couponTransactionDao.getTotalCountByByCouponId(action.getCouponId()));
		return details;
  }

	private TransactionDetails searchByAccountId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setTransactions(couponTransactionDao.findByAccountIdAndStatus(
				session.getAccount().getAccountId(), TransactionStatus.COMPLETE, action.getStart(), action.getPageSize()));
		details.setTotalTransactionCount(couponTransactionDao.getTotalCountByAccountIdAndStatus(
				session.getAccount().getAccountId(), TransactionStatus.COMPLETE));
		
		return details;
  }
	
	private static class TransactionDetails {
		private List<CouponTransaction> transactions = new ArrayList<CouponTransaction>();
		private Long totalTransactionCount = 0L;
		public List<CouponTransaction> getTransactions() {
	    return transactions;
    }

		public void setTransactions(List<CouponTransaction> transactions) {
	    this.transactions = transactions;
    }

		public Long getTotalTransactionCount() {
	    return totalTransactionCount;
    }

		public void setTotalTransactionCount(Long totalTransactionCount) {
	    this.totalTransactionCount = totalTransactionCount;
    }
	}
}
