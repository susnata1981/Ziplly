package com.ziplly.app.server.bli;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.server.crypto.CryptoUtil;

public class CouponCodeDetails {
  private static final String SEPARATOR = ":";
  private Long buyerAccountId;
  private Long sellerAccountId;
  private Long couponId;
  private Long orderId;
  private static CryptoUtil cryptoUtil;

  public CouponCodeDetails(CryptoUtil cryptoUtil) {
    CouponCodeDetails.cryptoUtil = cryptoUtil;
    checkNotNull(cryptoUtil);
  }
  
  public CouponCodeDetails setCouponId(Long couponId) {
    this.couponId = couponId;
    return this;
  }

  public CouponCodeDetails setSellerAccountId(Long sellerAccountId) {
    this.sellerAccountId = sellerAccountId;
    return this;
  }

  public CouponCodeDetails setBuyerAccountId(Long buyerAccountId) {
    this.buyerAccountId = buyerAccountId;
    return this;
  }

  public CouponCodeDetails setOrderId(Long orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * Encrypts the encoded coupon code
   */
  public String getEncryptedCouponCode() throws Exception {
    return cryptoUtil.encrypt(encode());
  }

  /**
   * Decrypts coupon code
   */
  public CouponCodeDetails decode(String encodedCouponData) throws InvalidCouponException,
      InvalidKeyException,
      NoSuchAlgorithmException,
      NoSuchPaddingException,
      IllegalBlockSizeException,
      BadPaddingException,
      UnsupportedEncodingException,
      IOException {

    checkNotNull(encodedCouponData);

    String[] tokens = decryptTokens(encodedCouponData);
    if (tokens.length != 4) {
      throw new InvalidCouponException(Arrays.toString(tokens));
    }

    CouponCodeDetails ccd = new CouponCodeDetails(cryptoUtil);
    ccd.setBuyerAccountId(Long.parseLong(tokens[0]));
    ccd.setSellerAccountId(Long.parseLong(tokens[1]));
    ccd.setCouponId(Long.parseLong(tokens[2]));
    ccd.setOrderId(Long.parseLong(tokens[3]));
    return ccd;
  }

  public String encode() {
    return buyerAccountId + SEPARATOR + sellerAccountId + SEPARATOR + couponId + SEPARATOR
        + orderId;
  }

  private static String[] decryptTokens(String encodedCouponData) throws InvalidKeyException,
      NoSuchAlgorithmException,
      NoSuchPaddingException,
      IllegalBlockSizeException,
      BadPaddingException,
      UnsupportedEncodingException,
      IOException {

    String decryptText = cryptoUtil.decrypt(encodedCouponData);
    System.out.println("Decrypted text=" + decryptText);
    return decryptText.split(SEPARATOR);
  }

  @Override
  public String toString() {
    return "BuyerAccountId=" + buyerAccountId + " SellerAcountId=" + sellerAccountId
        + " CouponId=" + couponId + " OrderId = " + orderId;
  }

  public Long getBuyerId() {
    return buyerAccountId;
  }

  public Long getSellerId() {
    return sellerAccountId;
  }

  public Long getCouponId() {
    return couponId;
  }

  public Long getOrderId() {
    return orderId;
  }
}
