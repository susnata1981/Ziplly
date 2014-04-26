package com.ziplly.app.server.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class CryptoUtil {
	private Key key;
	private Cipher cipher;

	@Inject
	public CryptoUtil(Cipher cipher, Key key) {
		this.cipher = cipher;
		this.key = key;
	}

	public String encrypt(String message) throws IllegalBlockSizeException,
	    BadPaddingException,
	    NoSuchAlgorithmException,
	    NoSuchPaddingException,
	    InvalidKeyException,
	    UnsupportedEncodingException, EncoderException {

		cipher.init(Cipher.ENCRYPT_MODE, key);

		// Gets the raw bytes to encrypt, UTF8 is needed for
		// having a standard character set
		byte[] stringBytes = message.getBytes("UTF8");

		// encrypt using the cypher
		byte[] raw = cipher.doFinal(stringBytes);

		// return base64;
		return new String(encode(raw));
	}

	public String decrypt(String encrypted) throws InvalidKeyException,
	    NoSuchAlgorithmException,
	    NoSuchPaddingException,
	    IllegalBlockSizeException,
	    BadPaddingException,
	    IOException {

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] raw = decode(encrypted);

		// decode the message
		byte[] stringBytes = cipher.doFinal(raw);

		// converts the decoded message to a String
		String clear = new String(stringBytes, "UTF8");
		return clear;
	}

	protected byte[] encode(byte[] data) throws EncoderException {
		return Base64.encodeBase64(data);
	}

	protected byte[] decode(String message) throws UnsupportedEncodingException {
		return Base64.decodeBase64(message.getBytes("utf-8"));
	}

	public static void main(String[] args) throws EncoderException, Exception {
		Injector injector = Guice.createInjector(new CryptoModule());
		CryptoUtil c = injector.getInstance(CryptoUtil.class);
		String msg = URLEncoder.encode("1:2:3", "utf-8");
		String encrypt = c.encrypt(msg);
		System.out.println("Encrypted:"+encrypt);
		String decrypt = c.decrypt(encrypt);
		System.out.println("Decrypted:"+decrypt);
		System.out.println("Text="+URLDecoder.decode(decrypt, "utf-8"));
	}
}