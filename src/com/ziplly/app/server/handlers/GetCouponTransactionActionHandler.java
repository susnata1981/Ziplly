package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.PurchasedCouponDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class GetCouponTransactionActionHandler extends AbstractAccountActionHandler<GetCouponTransactionAction, GetCouponTransactionResult>{
	private PurchasedCouponDAO purchasedCouponDao;

	@Inject
	public GetCouponTransactionActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      PurchasedCouponDAO purchasedCouponDao) {
		
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.purchasedCouponDao = purchasedCouponDao;
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
		result.setPurchasedCoupons(EntityUtil.clonePurchaseCouponList(details.getPurchasedCoupons()));
		result.setTotalTransactions(details.getTotalTransactionCount());
		return result;
  }

	private TransactionDetails searchByCouponId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setPurchasedCoupons(purchasedCouponDao.findTransactionByCouponId(action.getCouponId(), action.getStart(), action.getPageSize()));
		details.setTotalTransactionCount(purchasedCouponDao.getTotalCountByByCouponId(action.getCouponId()));
		return details;
  }

	private TransactionDetails searchByAccountId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setPurchasedCoupons(purchasedCouponDao.findByAccountIdAndStatus(
				session.getAccount().getAccountId(), TransactionStatus.COMPLETE, action.getStart(), action.getPageSize()));
		details.setTotalTransactionCount(purchasedCouponDao.getTotalByAccountIdAndStatus(
				session.getAccount().getAccountId(), TransactionStatus.COMPLETE));
		
		return details;
  }
	
	private static class TransactionDetails {
		private List<PurchasedCoupon> purchasedCoupons = new ArrayList<PurchasedCoupon>();
		private Long totalTransactionCount = 0L;
		
		public List<PurchasedCoupon> getPurchasedCoupons() {
	    return purchasedCoupons;
    }

		public void setPurchasedCoupons(List<PurchasedCoupon> purchasedCoupons) {
	    this.purchasedCoupons = purchasedCoupons;
    }

		public Long getTotalTransactionCount() {
	    return totalTransactionCount;
    }

		public void setTotalTransactionCount(Long totalTransactionCount) {
	    this.totalTransactionCount = totalTransactionCount;
    }
	}
}
