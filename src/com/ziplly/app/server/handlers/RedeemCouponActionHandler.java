package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.shared.RedeemCouponAction;
import com.ziplly.app.shared.RedeemCouponResult;

public class RedeemCouponActionHandler extends AbstractAccountActionHandler<RedeemCouponAction, RedeemCouponResult>{
	private CouponBLI couponBli;

	@Inject
	public RedeemCouponActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponBLI couponBli) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponBli = couponBli;
  }

	@Override
  public Class<RedeemCouponAction> getActionType() {
		return RedeemCouponAction.class;
  }

	@Override
  public RedeemCouponResult
      doExecute(RedeemCouponAction action, ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.getEncodedCouponCode());
		
		validateSession();
		Coupon coupon = couponBli.redeemCoupon(action.getEncodedCouponCode(), session.getAccount().getAccountId());
		
		RedeemCouponResult result = new RedeemCouponResult();
		result.setTweet(EntityUtil.clone(coupon.getTweet()));
		return result;
  }
}
