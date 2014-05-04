package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
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
	private Logger logger = Logger.getLogger(PurchaseCouponActionHandler.class.getName());
	
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

		logger.info(String.format("PurchaseCouponActionHandler called with buyerId %d, transactionId %s"
				, action.getBuyer().getAccountId(), action.getCouponTransactionId()));
		
		validateSession();
		CouponTransaction couponTransaction = couponTransactionDao.findCouponTransactionByIdAndStatus(
				Long.valueOf(action.getCouponTransactionId()), 
				TransactionStatus.PENDING);
		
		checkNotNull(couponTransaction);
		
		switch(action.getResultStatus()) {
			case SUCCESS :
				handleSuccess(action, couponTransaction);
				break;
			case FAILED :
				handleFailure(action, couponTransaction);
				break;
		}
		
		PurchaseCouponResult result = new PurchaseCouponResult();
		result.setCouponTransaction(EntityUtil.clone(couponTransaction));
		return result;
	}

	@Override
	public Class<PurchasedCouponAction> getActionType() {
		return PurchasedCouponAction.class;
	}
	
	private void handleSuccess(PurchasedCouponAction action, CouponTransaction couponTransaction) throws DispatchException {
		try {
			accountBli.checkAccountEligibleForCouponPurchase(session.getAccount(), action.getCoupon().getCouponId());
		} catch(DispatchException ex) {
			//TODO: log the exception
			couponTransaction.setStatus(TransactionStatus.ELIGIBILITY_FAILED);
			couponTransactionDao.save(couponTransaction);
			throw ex;
		}

		// Update quantity 
		couponTransaction.getCoupon().setQuantityPurchased(couponTransaction.getCoupon().getQuantityPurchased() + 1);
		
		//update the status
		couponTransaction.setStatus(TransactionStatus.PENDING_COMPLETE);
		
		// complete purchase in first callback
		boolean completePurchase = Boolean.valueOf(System.getProperty(
				StringConstants.COMPLETE_COUPON_PURCHASE_ON_FIRST_CALLBACK_FLAG, "true"));
		
		logger.info(String.format("CompletePurchase value = %s",Boolean.valueOf(completePurchase).toString()));

//		if(completePurchase) {
//			couponTransaction.setStatus(TransactionStatus.COMPLETE);
//		}
		
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
		
		//couponTransactionDao.save(couponTransaction);
		
		try {
			purchasedCoupon.setQrcode(couponBLI.getQrcode(couponTransaction));
			couponTransactionDao.update(couponTransaction);
		} catch (Exception e) {
			throw new InternalError(String.format("Failed to generate coupon code"));
		}
	}
	
	private void handleFailure(PurchasedCouponAction action, CouponTransaction couponTransaction) throws DispatchException {
		couponTransaction.setStatus(TransactionStatus.FAILURE);
		// Set update date
		Date now = new Date();
		couponTransaction.setTimeUpdated(now);
		couponTransactionDao.save(couponTransaction);
	}
}
