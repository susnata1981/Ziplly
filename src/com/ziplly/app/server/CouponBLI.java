package com.ziplly.app.server;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.ziplly.app.client.exceptions.InternalError;

public interface CouponBLI {
	String getQrcode(Long buyerAccountId, Long senderAccountId, Long couponId) throws InternalError;
	
	void markAsUsed(String url) 
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException;

	String getEncryptedQRCode(String qrcode);
}
