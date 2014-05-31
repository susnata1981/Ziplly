package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.TransactionDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.PurchasedCouponDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.shared.PurchaseCouponResult;
import com.ziplly.app.shared.PurchasedCouponAction;

public class PurchaseCouponActionHandler extends
    AbstractAccountActionHandler<PurchasedCouponAction, PurchaseCouponResult> {
	private TransactionDAO couponTransactionDao;
	private CouponBLI couponBLI;
	private Logger logger = Logger.getLogger(PurchaseCouponActionHandler.class.getName());
	private CouponDAO couponDao;
	private PurchasedCouponDAO purchasedCouponDao;
	
	@Inject
	public PurchaseCouponActionHandler(Provider<EntityManager> entityManagerProvider,
	    AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    CouponBLI couponBLI,
	    CouponDAO couponDao,
	    PurchasedCouponDAO purchasedCouponDao,
	    TransactionDAO couponTransactionDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.couponBLI = couponBLI;
		this.couponDao = couponDao;
		this.purchasedCouponDao = purchasedCouponDao;
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
		Transaction couponTransaction = couponTransactionDao.findByIdAndStatus(
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
	
	@Transactional
	private void handleSuccess(PurchasedCouponAction action, Transaction transaction) throws DispatchException {
		Coupon coupon = couponDao.findById(action.getCoupon().getCouponId());
		try {
			accountBli.checkAccountEligibleForCouponPurchase(session.getAccount(), coupon);
		} catch(DispatchException ex) {
			//TODO: log the exception
			transaction.setStatus(TransactionStatus.ELIGIBILITY_FAILED);
			couponTransactionDao.save(transaction);
			throw ex;
		}

		// Update quantity 
		coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
		
		//update the status
		transaction.setStatus(TransactionStatus.PENDING_COMPLETE);
		
		// complete purchase in first callback
		boolean completePurchase = Boolean.valueOf(System.getProperty(
				StringConstants.COMPLETE_COUPON_PURCHASE_ON_FIRST_CALLBACK_FLAG, "true"));
		
		logger.info(String.format("CompletePurchase value = %s", Boolean.valueOf(completePurchase).toString()));

		if(completePurchase) {
			transaction.setStatus(TransactionStatus.COMPLETE);
		}
		
		// Set update date
		Date now = new Date();
		transaction.setTimeUpdated(now);
		
		// Set QR code
		PurchasedCoupon purchasedCoupon = new PurchasedCoupon();
		purchasedCoupon.setCouponTransaction(transaction);
		purchasedCoupon.setStatus(PurchasedCouponStatus.UNUSED);
		purchasedCoupon.setTimeUpdated(now);
		purchasedCoupon.setTimeCreated(now);
		purchasedCoupon.setCoupon(coupon);
		purchasedCouponDao.save(purchasedCoupon);
		
		try {
			purchasedCoupon.setQrcode(couponBLI.getQrcode(purchasedCoupon));
			couponTransactionDao.update(transaction);
		} catch (Exception e) {
			throw new InternalError(String.format("Failed to generate coupon code"));
		}
	}
	
	private void handleFailure(PurchasedCouponAction action, Transaction couponTransaction) throws DispatchException {
		couponTransaction.setStatus(TransactionStatus.FAILURE);
		// Set update date
		Date now = new Date();
		couponTransaction.setTimeUpdated(now);
		couponTransactionDao.save(couponTransaction);
	}
}
