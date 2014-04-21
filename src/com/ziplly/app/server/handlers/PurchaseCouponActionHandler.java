package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.SoldOutException;
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
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.CouponBLI;
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
		checkIfAccountEligible(action.getBuyer(), action.getCoupon());
		
		// Update quantity 
		Coupon coupon = couponTransactionDao.findByCouponId(action.getCoupon().getCouponId());
		if (coupon.getQuantityPurchased() == coupon.getQuanity()) {
			// Log error
			throw new SoldOutException(String.format("Coupon: %s sold out", coupon.getDescription()));
		}
		
		coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
		
		CouponTransaction couponTransaction = new CouponTransaction();
		couponTransaction.setBuyer(EntityUtil.convert(action.getBuyer()));
		couponTransaction.setCoupon(coupon);
		couponTransaction.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
		couponTransaction.setStatus(TransactionStatus.ACTIVE);
		
		// Set date
		Date now = new Date();
		couponTransaction.setTimeUpdated(now);
		couponTransaction.setTimeCreated(now);
		
		// Set QR code
		PurchasedCoupon purchasedCoupon = new PurchasedCoupon();
		purchasedCoupon.setCouponTransaction(couponTransaction);
		purchasedCoupon.setStatus(PurchasedCouponStatus.UNUSED);
		purchasedCoupon.setQrcode(couponBLI.getQrcode(
				action.getBuyer().getAccountId(), 
				coupon.getTweet().getSender().getAccountId(),
				coupon.getCouponId()));
		
		purchasedCoupon.setTimeUpdated(now);
		purchasedCoupon.setTimeCreated(now);
		couponTransaction.setPurchasedCoupon(purchasedCoupon);
		
		couponTransactionDao.save(couponTransaction);
		
		PurchaseCouponResult result = new PurchaseCouponResult();
		result.setCouponTransaction(EntityUtil.clone(couponTransaction));
		return result;
	}

	private void checkIfAccountEligible(AccountDTO buyer, CouponDTO coupon) throws UsageLimitExceededException {
		checkArgument(session.getAccount().getAccountId() == buyer.getAccountId());
		
		// Should the publisher be allowed to buy??
		try {
			List<CouponTransaction> transactions =
			    couponTransactionDao.findCouponTransactionByAccountId(buyer.getAccountId());

			if (transactions.size() == 0) {
				return;
			}
			
			if (transactions.size() >= coupon.getNumberAllowerPerIndividual()) {
				throw new UsageLimitExceededException();
			}
		} catch (NoResultException nre) {
			return;
		}
	}

	@Override
	public Class<PurchasedCouponAction> getActionType() {
		return PurchasedCouponAction.class;
	}
}
