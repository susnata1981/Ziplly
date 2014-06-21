package com.ziplly.app.server.bli;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;

import javax.persistence.NoResultException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.CouponCampaignEndedException;
import com.ziplly.app.client.exceptions.CouponSoldOutException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.dao.OrderDAO;
import com.ziplly.app.model.CouponItemStatus;
import com.ziplly.app.model.OrderStatus;
import com.ziplly.app.model.TransactionStatus;
import com.ziplly.app.server.bli.ServiceModule.CouponRedeemEndpoint;
import com.ziplly.app.server.crypto.CryptoUtil;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.CouponItem;
import com.ziplly.app.server.model.jpa.Order;
import com.ziplly.app.server.model.jpa.OrderDetails;
import com.ziplly.app.server.model.jpa.Transaction;
import com.ziplly.app.shared.EmailTemplate;

public class CouponBLIImpl implements CouponBLI {
  private final String qrcodeEndpoint;
  private static CryptoUtil cryptoUtil;

  private Logger logger = Logger.getLogger(CouponBLIImpl.class.getName());
  private String couponRedeemEndpoint;
  private AccountBLI accountBli;
  private TweetNotificationBLI tweetNotificationBli;
  private OrderDAO orderDAO;

  @Inject
  public CouponBLIImpl(AccountBLI accountBli,
      TweetNotificationBLI tweetNotificationBli,
      @Named("qrcode_endpoint") String qrcodeEndpoint,
      @CouponRedeemEndpoint String couponRedeemEndpoint,
      CryptoUtil cryptoUtil,
      OrderDAO orderDAO) {

    this.accountBli = accountBli;
    this.tweetNotificationBli = tweetNotificationBli;
    this.qrcodeEndpoint = qrcodeEndpoint;
    this.couponRedeemEndpoint = couponRedeemEndpoint;
    CouponBLIImpl.cryptoUtil = cryptoUtil;
    this.orderDAO = orderDAO;
  }

  @Override
  public String getQrcode(long buyerAccountId, long sellerAccountId, long couponId, long orderId) {
    CouponCodeDetails ccd = new CouponCodeDetails(cryptoUtil);
    ccd
        .setBuyerAccountId(buyerAccountId)
        .setSellerAccountId(sellerAccountId)
        .setCouponId(couponId)
        .setOrderId(orderId);

    try {
      return ccd.getEncryptedCouponCode();
    } catch (Exception e) {
      throw new RuntimeException("Failed to encrypt code coupon code");
    }
  }

  @Override
  public String getQrcodeUrl(CouponItem pr) throws UnsupportedEncodingException {
    String redeemUrl = couponRedeemEndpoint + encode(pr.getQrcode());
    return qrcodeEndpoint + redeemUrl;
  }

  String encode(String code) throws UnsupportedEncodingException {
    checkNotNull(code);
    return URLEncoder.encode(code, "utf-8");
  }

  @Override
  public Coupon
      redeemCoupon(String encodedCouponData, Long sellerAccountId) throws InvalidCouponException,
          CouponAlreadyUsedException {

    checkArgument(encodedCouponData != null && sellerAccountId != null);

    CouponCodeDetails couponCodeDetails = new CouponCodeDetails(cryptoUtil);
    try {
      couponCodeDetails = couponCodeDetails.decode(decode(encodedCouponData));
    } catch (Exception e) {
      logger.severe(String.format("Unable to decrypt coupon code %s", encodedCouponData));
      throw new InvalidCouponException(encodedCouponData);
    }

    checkNotNull(couponCodeDetails);

    try {
      Order order = orderDAO.findById(couponCodeDetails.getOrderId());
      CouponItem item =
          orderDAO.findCouponItemByOrderAndCouponId(
              couponCodeDetails.getCouponId(),
              couponCodeDetails.getOrderId());

      if (order.getStatus() != OrderStatus.COMPLETED
          && order.getTransaction().getStatus() != TransactionStatus.COMPLETE) {
        throw new InvalidCouponException(encodedCouponData);
      }

      if (item.getStatus() == CouponItemStatus.USED) {
        throw new CouponAlreadyUsedException(encodedCouponData);
      }

      // Otherwise mark the coupon as used
      item.setStatus(CouponItemStatus.USED);
      item.setTimeUpdated(new Date());
      Coupon coupon = item.getCoupon();
      coupon.setQuantityPurchased(coupon.getQuantityPurchased() + 1);
      orderDAO.update(order);
      return coupon;
    } catch (NoResultException ex) {
      throw new InvalidCouponException("No coupon was found");
    }
  }

  @Override
  @Transactional
  public CouponItem createPendingTransaction(Account buyer, Coupon coupon) {
    Date now = new Date();

    CouponItem item = new CouponItem();
    item.setCoupon(coupon);
    item.setStatus(CouponItemStatus.PENDING);
    item.setTimeCreated(now);
    item.setTimeUpdated(now);

    OrderDetails orderDetails = new OrderDetails();
    // Users can only buy 1 coupon/person for now
    orderDetails.setQuantity(1);
    orderDetails.setItem(item);
    item.setOrderDetails(orderDetails);

    Transaction transaction = new Transaction();
    transaction.setBuyer(buyer);
    transaction.setStatus(TransactionStatus.PENDING);
    transaction.setAmount(coupon.getDiscountedPrice());
    transaction.setCurrency(Currency.getInstance(Locale.US).getCurrencyCode());
    transaction.setTimeCreated(now);
    transaction.setTimeUpdated(now);

    Order order = new Order();
    order.addOrderDetails(orderDetails);
    order.setTimeCreated(now);
    order.setTimeUpdated(now);
    order.setStatus(OrderStatus.PENDING);
    order.setTransaction(transaction);
    orderDetails.setOrder(order);
    orderDAO.save(order);
    return item;
  }

  @Override
  @Transactional
  public void completeTransaction(Long orderId) throws CouponSoldOutException,
      UsageLimitExceededException,
      CouponCampaignEndedException {

    logger.info(String.format("Complete transaction called for order %d", orderId));
    Date now = new Date();
    Order order = orderDAO.findById(orderId);
    checkNotNull(order);
    checkState(order.getStatus() == OrderStatus.PENDING);

    try {
      checkCouponEligibility(order.getTransaction().getBuyer(), order.getOrderDetails());
    } catch (CouponSoldOutException | UsageLimitExceededException | CouponCampaignEndedException ex) {
      logger.severe(String.format("Order %s eneligible for purchase", order));
      order.setStatus(OrderStatus.ELIGIBILITY_FAILED);
      order.getTransaction().setStatus(TransactionStatus.ELIGIBILITY_FAILED);
      orderDAO.update(order);
      throw ex;
    }

    Iterator<OrderDetails> iterator = order.getOrderDetails().iterator();
    while (iterator.hasNext()) {
      OrderDetails od = iterator.next();
      CouponItem item = od.getItem();

      // Set QR code
      item.setQrcode(getQrcode(order.getTransaction().getBuyer().getAccountId(), item
          .getCoupon()
          .getTweet()
          .getSender()
          .getAccountId(), item.getCoupon().getCouponId(), order.getId()));

      item.setStatus(CouponItemStatus.UNUSED);
      item.setTimeUpdated(now);
      item.setTimeCreated(now);

      // Update quantity
      item.getCoupon().incrementQuantityPurchased(od.getQuantity());
    }

    // Update the status
    Transaction transaction = order.getTransaction();
    transaction.setStatus(TransactionStatus.COMPLETE);
    transaction.setTimeUpdated(now);
    order.setStatus(OrderStatus.COMPLETED);

    orderDAO.update(order);
    tweetNotificationBli.sendCouponPurchaseNotification(
        order.getTransaction(),
        EmailTemplate.COUPON_PURCHASE);

    logger.info(String.format("Complete transaction finished for order %d", orderId));
  }

  private void
      checkCouponEligibility(Account buyer, Collection<OrderDetails> orderDetails) throws CouponSoldOutException,
          UsageLimitExceededException,
          CouponCampaignEndedException {

    Iterator<OrderDetails> iterator = orderDetails.iterator();
    while (iterator.hasNext()) {
      OrderDetails next = iterator.next();
      assert (next.getQuantity() == 1);
      accountBli.checkAccountEligibleForCouponPurchase(buyer, next.getItem().getCoupon());
    }
  }

  private String decode(String encodedString) throws UnsupportedEncodingException {
    checkNotNull(encodedString);
    return URLDecoder.decode(encodedString, "utf-8");
  }
}
