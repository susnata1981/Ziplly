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
import com.ziplly.app.dao.OrderDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.server.model.jpa.Order;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;

public class GetCouponQRCodeUrlActionHandler extends AbstractAccountActionHandler<GetCouponQRCodeUrlAction, GetCouponQRCodeUrlResult>{
	
	private CouponBLI couponBli;
	private OrderDAO orderDao;

	@Inject
	public GetCouponQRCodeUrlActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponBLI couponBli,
      OrderDAO orderDao) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponBli = couponBli;
	  this.orderDao = orderDao;
  }

	@Override
  public GetCouponQRCodeUrlResult doExecute(GetCouponQRCodeUrlAction action,
      ExecutionContext context) throws DispatchException {
		
		checkNotNull(action.orderId());
		
		validateSession();

		GetCouponQRCodeUrlResult result = new GetCouponQRCodeUrlResult();
		try {
			Order order = orderDao.findById(action.orderId());
			
			// Check if the current user is the buyer
			if (!order.getTransaction().getBuyer().equals(session.getAccount())) {
				throw new AccessException(String.format("User doesn't have access to this resource"));
			}
			
			CouponItem item = orderDao.findCouponItemByOrderAndCouponId(
			    order.getId(), action.getCouponId());
			String qrcodeUrl = couponBli.getQrcodeUrl(item);
			result.setUrl(qrcodeUrl);
			result.setCoupon(EntityUtil.clone(item.getCoupon()));
			result.setSeller(EntityUtil.convert(item.getCoupon().getTweet().getSender()));
			result.setBuyer(EntityUtil.convert(order.getTransaction().getBuyer()));
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
