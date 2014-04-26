package com.ziplly.app.server.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CryptoModule extends PrivateModule {

	private static byte [] keyData = {
		2,10,13,15,1,10,12,19
//		3,2,10,13,15,1,10,12
	};
	
	@Override
	protected void configure() {
		bind(CryptoUtil.class).in(Singleton.class);
		expose(CryptoUtil.class);
	}

	@Provides
	@Singleton
	Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
//		return Cipher.getInstance("DES/ECB/PKCS5Padding");
		return Cipher.getInstance("DES/ECB/PKCS5Padding");
//		return Cipher.getInstance("AES/CBC/PKCS5Padding");
	}
	
	@Provides
	@Singleton
	Key generateKey() throws NoSuchAlgorithmException, InvalidKeyException {
//		KeyGenerator generator;
//		generator = KeyGenerator.getInstance("DES");
//		generator.init(56); 
//		return generator.generateKey();
		DESKeySpec desKeySpec = new DESKeySpec(keyData);
		SecretKeySpec secretKeySpec = new SecretKeySpec(desKeySpec.getKey(), "DES");
		return secretKeySpec;
	}
	
//	@Provides
//	@Singleton
//	Key generateKey() throws NoSuchAlgorithmException, InvalidKeyException {
//		MessageDigest sha = MessageDigest.getInstance("SHA-1");
//		byte [] key = sha.digest(keyData);
//		key = Arrays.copyOf(key, 16); // use only first 128 bit
//
//		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//		return secretKeySpec;
//	}
	
	@Provides
	public SecureRandom getSecureRandom() {
		return new SecureRandom();
	}
}
