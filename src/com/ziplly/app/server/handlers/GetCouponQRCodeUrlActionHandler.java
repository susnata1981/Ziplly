package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.PurchasedCouponDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.model.jpa.PurchasedCoupon;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class GetCouponQRCodeUrlActionHandler extends AbstractAccountActionHandler<GetCouponQRCodeUrlAction, GetCouponQRCodeUrlResult>{
	
	private CouponBLI couponBli;
	private PurchasedCouponDAO purhchasedCouponDAO;

	@Inject
	public GetCouponQRCodeUrlActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponBLI couponBli,
      PurchasedCouponDAO purhchasedCouponDAO) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponBli = couponBli;
	  this.purhchasedCouponDAO = purhchasedCouponDAO;
  }

	@Override
  public GetCouponQRCodeUrlResult doExecute(GetCouponQRCodeUrlAction action,
      ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.getCouponTransactionId());
		
		validateSession();

		GetCouponQRCodeUrlResult result = new GetCouponQRCodeUrlResult();
		try {
			PurchasedCoupon pc = purhchasedCouponDAO.findById(action.getCouponTransactionId());
			
			// Check if the current user is the buyer
			if (!pc.getTransaction().getBuyer().equals(session.getAccount())) {
				throw new AccessException(String.format("User doesn't have access to this resource"));
			}
			
			String qrcodeUrl = couponBli.getQrcodeUrl(pc);
			result.setUrl(qrcodeUrl);
			result.setCoupon(EntityUtil.clone(pc.getCoupon()));
			result.setSeller(EntityUtil.convert(pc.getCoupon().getTweet().getSender()));
			result.setBuyer(EntityUtil.convert(pc.getTransaction().getBuyer()));
		} catch(NoResultException nre) {
			throw new AccessException();
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("Problem encountered in reading coupon");
    }
		
		return result;
  }

	@Override
  public Class<GetCouponQRCodeUrlAction> getActionType() {
		return GetCouponQRCodeUrlAction.class;
  }
}
