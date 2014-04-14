package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.CouponCampaignEnded;
import com.ziplly.app.client.exceptions.CouponCampaignNotStarted;
import com.ziplly.app.client.exceptions.SoldOutException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;

public class CheckBuyerEligibilityForCouponActionHandler 
	extends AbstractAccountActionHandler<CheckBuyerEligibilityForCouponAction, CheckBuyerEligibilityForCouponResult>{

	private CouponTransactionDAO couponTransactionDao;
	
	@Inject
	public CheckBuyerEligibilityForCouponActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponTransactionDAO couponTransactionDao) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponTransactionDao = couponTransactionDao;
  }

	@Override
  public CheckBuyerEligibilityForCouponResult
      doExecute(CheckBuyerEligibilityForCouponAction action, ExecutionContext context) throws DispatchException {
		
		checkArgument(action.getCoupon() != null);
		checkNotNull(action.getBuyer());
		//TODO(shaan): Create a pending transaction here, so in case there's an error after
		// the user pays, we can resolve it.
		
		Coupon coupon = couponTransactionDao.findByCouponId(action.getCoupon().getCouponId());
		//Check coupon quantity availability 
		if (coupon.getQuantityPurchased() == coupon.getQuanity()) {
			// Log error
			throw new SoldOutException(String.format("Coupon: %s sold out", coupon.getDescription()));
		}
		
		// Should the publisher be allowed to buy??
		try {
			List<CouponTransaction> transactions =
			    couponTransactionDao.findCouponTransactionByAccountId(action.getBuyer().getAccountId());
			
			if (transactions.size() >= coupon.getNumberAllowerPerIndividual()) {
				throw new UsageLimitExceededException("You have previously purchased the coupon.");
			}
		} catch (NoResultException nre) {
			//Log the exception but this is expected if the buyer did not purchase these coupons previously.
		}
		
		// check date validity
		// The Buy button should be disabled in the view for these date validity fail. 
		Date now = new Date();
		if(now.before(coupon.getStartDate())) {
			throw new CouponCampaignNotStarted(coupon.getDescription());
		}
		
		if(now.after(coupon.getEndDate())) {
			throw new CouponCampaignEnded(coupon.getDescription());
		}
		
		return new CheckBuyerEligibilityForCouponResult();
  }

	@Override
  public Class<CheckBuyerEligibilityForCouponAction> getActionType() {
		return CheckBuyerEligibilityForCouponAction.class;
  }
}
