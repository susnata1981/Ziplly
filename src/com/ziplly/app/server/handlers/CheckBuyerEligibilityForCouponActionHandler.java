package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.PaymentService;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;

public class CheckBuyerEligibilityForCouponActionHandler 
	extends AbstractAccountActionHandler<CheckBuyerEligibilityForCouponAction, CheckBuyerEligibilityForCouponResult>{

	private PaymentService paymentService;
	private CouponTransactionDAO couponTransactionDao;
	private CouponDAO couponDao;

	@Inject
	public CheckBuyerEligibilityForCouponActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponDAO couponDao,
      CouponTransactionDAO couponTransactionDao,
      PaymentService paymentService) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponDao = couponDao;
	  this.couponTransactionDao = couponTransactionDao;
	  this.paymentService = paymentService;
  }

	@Override
  public CheckBuyerEligibilityForCouponResult
      doExecute(CheckBuyerEligibilityForCouponAction action, ExecutionContext context) throws DispatchException {
		
		checkArgument(action.getCoupon() != null);
		Coupon coupon = couponDao.findById(action.getCoupon().getCouponId());
		
		validateSession();
		accountBli.checkAccountEligibleForCouponPurchase(session.getAccount(),action.getCoupon().getCouponId());
		try {
			CouponTransaction cTransaction = new CouponTransaction();
			cTransaction.setBuyer(session.getAccount());
			cTransaction.setCoupon(coupon);
			cTransaction.setStatus(TransactionStatus.PENDING);
			cTransaction.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
			Date now = new Date();
			cTransaction.setTimeCreated(now);
			cTransaction.setTimeUpdated(now);
			couponTransactionDao.save(cTransaction);
			
	    String jwtToken = paymentService.getJWTTokenForCoupon(
	    		cTransaction.getTransactionId(), 
	    		action.getCoupon(), 
	    		session.getAccount().getAccountId());
	    
	    CheckBuyerEligibilityForCouponResult result = new CheckBuyerEligibilityForCouponResult();
	    result.setJwtToken(jwtToken);
	    return result;
    } catch (Exception e) {
    	throw new InternalError("Couldn't create payment token");
    }
  }

	@Override
  public Class<CheckBuyerEligibilityForCouponAction> getActionType() {
		return CheckBuyerEligibilityForCouponAction.class;
  }
}
