package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.PaymentService;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.PurchasedCoupon;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;

public class CheckBuyerEligibilityForCouponActionHandler 
	extends AbstractAccountActionHandler<CheckBuyerEligibilityForCouponAction, CheckBuyerEligibilityForCouponResult>{

	private PaymentService paymentService;
	private CouponDAO couponDao;
	private CouponBLI couponBli;

	@Inject
	public CheckBuyerEligibilityForCouponActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponBLI couponBli,
      CouponDAO couponDao,
      PaymentService paymentService) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponBli = couponBli;
	  this.couponDao = couponDao;
	  this.paymentService = paymentService;
  }

	@Override
  public CheckBuyerEligibilityForCouponResult
      doExecute(CheckBuyerEligibilityForCouponAction action, ExecutionContext context) throws DispatchException {
		
		checkArgument(action.getCoupon() != null);
		validateSession();
		
		Coupon coupon = couponDao.findById(action.getCoupon().getCouponId());
		
		accountBli.checkAccountEligibleForCouponPurchase(session.getAccount(), coupon);
		try {
			PurchasedCoupon pc = couponBli.createPendingTransaction(session.getAccount(), coupon);
			
	    String jwtToken = paymentService.generateJWTTokenForCoupon(
	    		pc.getPurchasedCouponId(), 
	    		coupon, 
	    		session.getAccount().getAccountId());
	    
	    CheckBuyerEligibilityForCouponResult result = new CheckBuyerEligibilityForCouponResult();
	    result.setJwtToken(jwtToken);
	    return result;
    } catch (Exception e) {
    	throw new InternalException("Couldn't create payment token");
    }
  }

	@Override
  public Class<CheckBuyerEligibilityForCouponAction> getActionType() {
		return CheckBuyerEligibilityForCouponAction.class;
  }
}
