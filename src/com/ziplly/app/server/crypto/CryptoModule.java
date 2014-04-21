package com.ziplly.app.server.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CryptoModule extends PrivateModule {

	@Override
	protected void configure() {
		bind(CryptoUtil.class).in(Singleton.class);
		expose(CryptoUtil.class);
	}

	@Provides
	@Singleton
	Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance("DES/ECB/PKCS5Padding");
	}
	
	@Provides
	@Singleton
	Key generateKey() throws NoSuchAlgorithmException {
		KeyGenerator generator;
		generator = KeyGenerator.getInstance("DES");
		generator.init(new SecureRandom()); 
		return generator.generateKey();
	}
}
