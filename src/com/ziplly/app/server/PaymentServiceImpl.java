package com.ziplly.app.server;
	
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.Instant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

public class PaymentServiceImpl implements PaymentService {
	private static final String SELLER_ID = "15732117996604841954";
	private static final String SELLER_SECRET = "zYfFCJLbvBgnVsAxa0wMpQ";

	@Override
	public String getJWT(Long sellerId, Double amount) throws InvalidKeyException,
			SignatureException {
		JsonToken token = null;
		token = createToken(sellerId, amount);
		return token.serializeAndSign();
	}

	JsonToken createToken(Long sellerId, Double amount) throws InvalidKeyException {
		// Current time and signing algorithm
		Calendar cal = Calendar.getInstance();
		HmacSHA256Signer signer = new HmacSHA256Signer(SELLER_ID, null,
				SELLER_SECRET.getBytes());

		// Configure JSON token
		JsonToken token = new JsonToken(signer);
		token.setAudience("Google");
		token.setParam("typ", "google/payments/inapp/item/v1");
		token.setIssuedAt(new Instant(cal.getTimeInMillis()));
		token.setExpiration(new Instant(cal.getTimeInMillis() + 60000L));

		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty("name", "ziplly marketing service");
		request.addProperty("description", "ziplly marketing service fee");
		request.addProperty("price", amount);
		request.addProperty("currencyCode", "USD");
		request.addProperty("sellerData", "seller_id:" + sellerId);

		JsonObject payload = token.getPayloadAsJsonObject();
		payload.add("request", request);

		return token;
	}

	public String deserialize(String tokenString) {
		String[] pieces = splitTokenString(tokenString);
		String jwtPayloadSegment = pieces[1];
		JsonParser parser = new JsonParser();
//		JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64
//				.decodeBase64(jwtPayloadSegment)));
		JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64
				.decodeBase64(jwtPayloadSegment.getBytes())));

		return payload.toString();
	}

	/**
	 * @param tokenString
	 *            The original encoded representation of a JWT
	 * @return Three components of the JWT as an array of strings
	 */
	private String[] splitTokenString(String tokenString) {
		String[] pieces = tokenString.split(Pattern.quote("."));
		if (pieces.length != 3) {
			throw new IllegalStateException(
					"Expected JWT to have 3 segments separated by '" + "."
							+ "', but it has " + pieces.length + " segments");
		}
		return pieces;
	}
}
