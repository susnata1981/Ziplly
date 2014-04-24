package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponTransactionDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.CouponTransaction;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class GetCouponQRCodeUrlActionHandler extends AbstractAccountActionHandler<GetCouponQRCodeUrlAction, GetCouponQRCodeUrlResult>{
	
	private CouponTransactionDAO couponTransactionDao;
	private CouponBLI couponBli;

	@Inject
	public GetCouponQRCodeUrlActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponBLI couponBli,
      CouponTransactionDAO couponTransactionDAO) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponBli = couponBli;
	  this.couponTransactionDao = couponTransactionDAO;
  }

	@Override
  public GetCouponQRCodeUrlResult doExecute(GetCouponQRCodeUrlAction action,
      ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.getCouponTransactionId());
		
		validateSession();

		GetCouponQRCodeUrlResult result = new GetCouponQRCodeUrlResult();
		try {
			CouponTransaction txn = couponTransactionDao.findById(action.getCouponTransactionId());
			String qrcode = couponBli.getQrcodeUrl(txn.getBuyer().getAccountId(), 
					txn.getCoupon().getTweet().getSender().getAccountId(), 
					txn.getCoupon().getCouponId());
			result.setUrl(qrcode);
		} catch(NoResultException nre) {
			throw new AccessError();
		}
		
		return result;
  }

	@Override
  public Class<GetCouponQRCodeUrlAction> getActionType() {
		return GetCouponQRCodeUrlAction.class;
  }
}
