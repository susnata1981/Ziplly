package com.ziplly.app.server;

import static org.junit.Assert.assertEquals;

import java.net.URLDecoder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.CouponBLIImpl;
import com.ziplly.app.server.crypto.CryptoUtil;

@RunWith(JUnit4.class)
public class CouponBLIImplTest extends AbstractBase {
	private CouponBLI couponBli;
	private CryptoUtil cryptoUtil;

	@Before
	public void setUp() {
		couponBli = getInstance(CouponBLI.class);
		cryptoUtil = getInstance(CryptoUtil.class);
	}

	@Test
	public void testEncryption() throws Exception {
		Long buyerAccountId = 1L;
		Long sellerAccountId = 2L;
		Long couponId = 3L;
		String qrcode = couponBli.getQrcodeUrl(buyerAccountId, sellerAccountId, couponId);
		String encryptedQrcode = couponBli.getEncryptedQRCode(qrcode);

		String decryptedMessage = cryptoUtil.decrypt(URLDecoder.decode(encryptedQrcode, "utf-8"));
		assertEquals(CouponBLIImpl.encode(buyerAccountId, sellerAccountId, couponId), decryptedMessage);
	}
}
