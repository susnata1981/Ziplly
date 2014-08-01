package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.CouponDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.OrderDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.CouponItemDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.CouponSalesDataBuilder;
import com.ziplly.app.server.bli.CouponSalesDataType;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.shared.GetCouponsAction;
import com.ziplly.app.shared.GetCouponsResult;

public class GetCouponsActionHandler extends AbstractAccountActionHandler<GetCouponsAction, GetCouponsResult>{
	private CouponDAO couponDao;
  private OrderDAO orderDao;
  private CouponSalesDataBuilder dataBuilder;

	@Inject
	public GetCouponsActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      AccountBLI accountBli,
      CouponDAO couponDao,
      OrderDAO orderDao) {
	  super(entityManagerProvider, accountDao, sessionDao, accountBli);
	  this.couponDao = couponDao;
	  this.orderDao = orderDao;
	  this.dataBuilder = new CouponSalesDataBuilder();
  }

	@Override
  public Class<GetCouponsAction> getActionType() {
		return GetCouponsAction.class;
  }

	@Override
  public GetCouponsResult
      doExecute(GetCouponsAction action, ExecutionContext context) throws DispatchException {
		
		checkArgument(action.getAccountId() == session.getAccount().getAccountId());
		checkArgument(action.getPageSize() > 0);

		GetCouponsResult result = new GetCouponsResult();
		List<Coupon> coupons = couponDao.findCouponsByAccountId(action.getAccountId(), action.getStart(), action.getPageSize());
		
		for(Coupon coupon : coupons) {
		  List<CouponItem> couponItems = loadCouponTransactions(coupon.getCouponId(), 0, Integer.MAX_VALUE);
		  List<CouponItemDTO> couponItemDtos = EntityUtil.cloneCouponItems(couponItems);
		  result.addTransactions(EntityUtil.clone(coupon), couponItemDtos);
		}
		
		
		long totalCouponCount = couponDao.getTotalCouponCountByAccountId(action.getAccountId());
		result.setTotalCouponCount(totalCouponCount);
		result.setSalesAmountData(dataBuilder.getSalesData(CouponSalesDataType.SALES_AMOUNT, 30, result.getCouponTransactionMap()));
		return result;
  }
	
	private List<CouponItem> loadCouponTransactions(long couponId, int pageStart, int pageSize) {
    return orderDao.findTransactionByCouponId(
        couponId,
        pageStart,
        pageSize);
  }
}
