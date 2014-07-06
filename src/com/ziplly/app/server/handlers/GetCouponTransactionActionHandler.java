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
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.OrderDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class GetCouponTransactionActionHandler extends AbstractAccountActionHandler<GetCouponTransactionAction, GetCouponTransactionResult>{
	private OrderDAO orderDao;
 private CouponDAO couponDao;

	@Inject
	public GetCouponTransactionActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponDAO couponDao,
      OrderDAO orderDao) {
		
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponDao = couponDao;
	  this.orderDao = orderDao;
  }

	@Override
  public Class<GetCouponTransactionAction> getActionType() {
		return GetCouponTransactionAction.class;
  }

	@Override
  public GetCouponTransactionResult doExecute(GetCouponTransactionAction action,
      ExecutionContext context) throws DispatchException {

    checkArgument(action.getStart() >= 0);
    checkArgument(action.getPageSize() > 0);

		validateSession();
		TransactionDetails details = null;
		switch(action.getSearchType()) {
			case BY_ACCOUNT_ID:
				details = searchByAccountId(action);
				break;
			case BY_COUPON_ID:
				checkNotNull(action.getCouponId());
				details = searchByCouponId(action);
		}
		 
		GetCouponTransactionResult result = new GetCouponTransactionResult();
		result.setPurchasedCoupons(EntityUtil.clonePurchaseCouponList(details.getPurchasedCoupons()));
		result.setTotalTransactions(details.getTotalTransactionCount());
		
		if (action.getCouponId() != null) {
		  result.setCoupon(EntityUtil.clone(details.getCoupon()));
		}
		return result;
  }

	private TransactionDetails searchByCouponId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setPurchasedCoupons(orderDao.findTransactionByCouponId(action.getCouponId(), action.getStart(), action.getPageSize()));
		details.setTotalTransactionCount(orderDao.getTotalCountByByCouponId(action.getCouponId()));
		details.setCoupon(couponDao.findById(action.getCouponId()));
		return details;
  }

	private TransactionDetails searchByAccountId(GetCouponTransactionAction action) {
		TransactionDetails details = new TransactionDetails();
		details.setPurchasedCoupons(orderDao.findByAccountIdAndStatus(
				session.getAccount().getAccountId(), 
				TransactionStatus.COMPLETED, 
				action.getStart(), 
				action.getPageSize()));
		
		details.setTotalTransactionCount(orderDao.getTotalCountByAccountIdAndStatus(
				session.getAccount().getAccountId(), TransactionStatus.COMPLETED));
		return details;
  }
	
	private static class TransactionDetails {
		private List<CouponItem> purchasedCoupons = new ArrayList<CouponItem>();
		private Long totalTransactionCount = 0L;
		private Coupon coupon;
		
		public List<CouponItem> getPurchasedCoupons() {
	    return purchasedCoupons;
    }

		public void setPurchasedCoupons(List<CouponItem> purchasedCoupons) {
	    this.purchasedCoupons = purchasedCoupons;
    }

		public Long getTotalTransactionCount() {
	    return totalTransactionCount;
    }

		public void setTotalTransactionCount(Long totalTransactionCount) {
	    this.totalTransactionCount = totalTransactionCount;
    }

    public Coupon getCoupon() {
     return coupon;
    }

    public void setCoupon(Coupon coupon) {
     this.coupon = coupon;
    }
	}
}
