package com.ziplly.app.server.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import static com.google.common.base.Preconditions.checkArgument;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetCouponsAction;
import com.ziplly.app.shared.GetCouponsResult;

public class GetCouponsActionHandler extends AbstractAccountActionHandler<GetCouponsAction, GetCouponsResult>{

	private CouponDAO couponDao;

	@Inject
	public GetCouponsActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponDAO couponDao) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponDao = couponDao;
  }

	@Override
  public Class<GetCouponsAction> getActionType() {
		return GetCouponsAction.class;
  }

	@Override
  public GetCouponsResult
      doExecute(GetCouponsAction action, ExecutionContext context) throws DispatchException {
		
		validateSession();
		checkArgument(action.getAccountId() == session.getAccount().getAccountId());
		checkArgument(action.getPageSize() > 0);

		List<Coupon> coupons = couponDao.findCouponsByAccountId(action.getAccountId(), action.getStart(), action.getPageSize());
		Long totalCouponCount = couponDao.getTotalCouponCountByAccountId(action.getAccountId());
		List<CouponDTO> couponList = EntityUtil.cloneCouponList(coupons);
		GetCouponsResult result = new GetCouponsResult();
		result.getCoupons().addAll(couponList);
		result.setTotalCouponCount(totalCouponCount);
		return result;
  }
}
