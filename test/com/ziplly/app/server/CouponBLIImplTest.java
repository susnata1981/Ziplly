package com.ziplly.app.server;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.base.AbstractBase;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.CouponBLIImpl.CouponCodeDetails;

@RunWith(JUnit4.class)
public class CouponBLIImplTest extends AbstractBase {
	private CouponBLI couponBli;

	@Before
	public void setUp() {
		couponBli = getInstance(CouponBLI.class);
	}

	@Test
	public void testEncryption() throws Exception {
		for(Long buyerId : buyerIds) {
			for(Long sellerId : sellerIds) {
				for(Long couponId : couponIds) {
					for(Long transactionId : transactionIds) {
						Transaction txn = createCouponTransaction(buyerId, sellerId, couponId, transactionId);
						
						String qrcode = couponBli.getQrcode(txn);
						
						CouponCodeDetails ccd = CouponCodeDetails.decode(qrcode);
						
						assertEquals(buyerId, ccd.getBuyerId());
						assertEquals(sellerId, ccd.getSellerId());
						assertEquals(couponId, ccd.getCouponId());
						assertEquals(transactionId, ccd.getCouponTransactionId());
					}
				}
			}
		}
	}
	
	private Transaction createCouponTransaction(Long buyerId, Long sellerId, Long couponId, Long transactionId) {
		Transaction ct = new Transaction();
		ct.setTransactionId(transactionId);
		Account buyer = new Account();
		buyer.setAccountId(buyerId);
		Account seller = new Account();
		seller.setAccountId(sellerId);
		Tweet t = new Tweet();
		t.setSender(seller);
		Coupon c = new Coupon();
		c.setCouponId(couponId);
		c.setTweet(t);
		ct.setCoupon(c);
		ct.setBuyer(buyer);
		return ct;
	}
}
