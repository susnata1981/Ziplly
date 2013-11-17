package com.ziplly.app.server;

import java.security.InvalidKeyException;
import java.security.SignatureException;

public interface PaymentService {
	String getJWT(Long sellerId, Double d) throws InvalidKeyException,
			SignatureException;
}
