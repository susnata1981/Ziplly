package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.exceptions.CouponSoldOutException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.shared.PurchaseCouponResult;
import com.ziplly.app.shared.PurchasedCouponAction;

public class PurchaseCouponActionHandler extends
    AbstractAccountActionHandler<PurchasedCouponAction, PurchaseCouponResult> {
	private CouponTransactionDAO couponTransactionDao;
	private CouponBLI couponBLI;

	@Inject
	public PurchaseCouponActionHandler(Provider<EntityManager> entityManagerProvider,
	    AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    CouponBLI couponBLI,
	    CouponTransactionDAO couponTransactionDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.couponBLI = couponBLI;
		this.couponTransactionDao = couponTransactionDao;
	}

	@Override
	public PurchaseCouponResult
	    doExecute(PurchasedCouponAction action, ExecutionContext context) throws DispatchException {

		checkNotNull(action.getBuyer());
		checkNotNull(action.getCoupon());

		validateSession();
		CouponTransaction couponTransaction = couponTransactionDao.findCouponTransactionByIdAndStatus(Long.valueOf(""+ action.getCouponTransactionId()), TransactionStatus.PENDING);
		checkNotNull(couponTransaction);
		
		try {
			accountBli.checkAccountEligibleForCouponPurchase(session.getAccount(), action.getCoupon().getCouponId());
		} catch(DispatchException ex) {
			//TODO: log the exception
			couponTransaction.setStatus(TransactionStatus.ELIGIBILITY_FAILED);
			couponTransactionDao.save(couponTransaction);
			throw ex;
		}
		
		// Update quantity 
		Coupon coupon = couponTransactionDao.findByCouponId(action.getCoupon().getCouponId());
		coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
		
		couponTransaction.setStatus(TransactionStatus.PENDING_COMPLETE);
		//In dev environment set the transaction to complete
		if(getEnvironment() == Environment.DEVEL) {
			couponTransaction.setStatus(TransactionStatus.COMPLETE);
		}
		
		// Set update date
		Date now = new Date();
		couponTransaction.setTimeUpdated(now);
		
		// Set QR code
		PurchasedCoupon purchasedCoupon = new PurchasedCoupon();
		purchasedCoupon.setCouponTransaction(couponTransaction);
		purchasedCoupon.setStatus(PurchasedCouponStatus.UNUSED);
		purchasedCoupon.setTimeUpdated(now);
		purchasedCoupon.setTimeCreated(now);
		couponTransaction.setPurchasedCoupon(purchasedCoupon);
		
		couponTransactionDao.save(couponTransaction);
		
		try {
	    purchasedCoupon.setQrcode(couponBLI.getQrcode(couponTransaction));
	    couponTransactionDao.update(couponTransaction);
    } catch (Exception e) {
    	throw new InternalError(String.format("Failed to generate coupon code"));
    }
		
		PurchaseCouponResult result = new PurchaseCouponResult();
		result.setCouponTransaction(EntityUtil.clone(couponTransaction));
		return result;
	}

	@Override
	public Class<PurchasedCouponAction> getActionType() {
		return PurchasedCouponAction.class;
	}
	
	public Environment getEnvironment() {
		Environment env =
		    (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
		        ? Environment.PROD : Environment.DEVEL;
		return env;
	}
}
