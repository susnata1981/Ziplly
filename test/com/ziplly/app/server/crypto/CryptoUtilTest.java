package com.ziplly.app.server.crypto;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.EncoderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.base.AbstractBase;
import com.ziplly.app.server.bli.CouponCodeDetails;

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
					for(long orderId : orderIds) {
						CouponCodeDetails ccd = createCouponCodeDetails(buyerId, sellerId, couponId, orderId);
						
						String text = ccd.encode();
						System.out.println("T = "+text);
						String encryptedText = cryptoUtil.encrypt(text);
						System.out.println("E = "+encryptedText);
						assertEquals(text, cryptoUtil.decrypt(encryptedText));
					}
				}
			}
		}
	}
					
	@Test
	public void decryptTest() throws Exception {
	  CouponCodeDetails ccd = createCouponCodeDetails(1L, 1L, 2L, 6L);
	  String encryptedText = cryptoUtil.encrypt(ccd.encode());
	  System.out.println(encryptedText);
	  String decrypt = cryptoUtil.decrypt(encryptedText);
	  System.out.println("Decrypted text = "+decrypt);
	}
	
	CouponCodeDetails createCouponCodeDetails(Long buyerId, Long sellerId, Long couponId, Long transactionId) {
		CouponCodeDetails ccd = new CouponCodeDetails(null);
		ccd.setBuyerAccountId(buyerId)
		   .setSellerAccountId(sellerId)
		   .setCouponId(couponId)
		   .setOrderId(transactionId);
		
		return ccd;
	}
}
