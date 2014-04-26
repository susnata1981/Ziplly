package com.ziplly.app.server.crypto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.base.AbstractBase;
import com.ziplly.app.server.bli.CouponBLIImpl.CouponCodeDetails;

@RunWith(JUnit4.class)
public class CryptoUtilTest extends AbstractBase {
	private CryptoUtil cryptoUtil;

	@Before
	public void setUp() {
		cryptoUtil = getInstance(CryptoUtil.class);
	}
	
	@Test
	public void testEncryption() throws Exception {
		for(Long buyerId : buyerIds) {
			for(Long sellerId : sellerIds) {
				for(Long couponId : couponIds) {
					for(Long transactionId : transactionIds) {
						CouponCodeDetails ccd = createCouponCodeDetails(buyerId, sellerId, couponId, transactionId);
						
						String text = ccd.encode();
						
						String encryptedText = cryptoUtil.encrypt(text);
						
						assertEquals(text, cryptoUtil.decrypt(encryptedText));
					}
				}
			}
		}
	}
					
	CouponCodeDetails createCouponCodeDetails(Long buyerId, Long sellerId, Long couponId, Long transactionId) {
		CouponCodeDetails ccd = new CouponCodeDetails();
		ccd.setBuyerAccountId(buyerId)
		   .setSellerAccountId(sellerId)
		   .setCouponId(couponId)
		   .setCouponTransactionId(transactionId);
		
		return ccd;
	}
}
