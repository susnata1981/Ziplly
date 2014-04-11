package com.ziplly.app.server;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.Instant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponDTO;

public class PaymentServiceImpl implements PaymentService {
	private static final String SELLER_ID = "15732117996604841954";
	private static final String SELLER_SECRET = "zYfFCJLbvBgnVsAxa0wMpQ";
	private static final String ZIPLLY_INC = "Ziplly Inc.";

	@Override
	public String getJWT(Long sellerId, Double amount) throws InvalidKeyException, SignatureException {
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty("name", "ziplly marketing service");
		request.addProperty("description", "ziplly marketing service fee");
		request.addProperty("price", amount);
		request.addProperty("currencyCode", Currency.getInstance(Locale.US).getCurrencyCode());
		request.addProperty("sellerData", "seller_id:" + sellerId);
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}

	public String deserialize(String tokenString) {
		String[] pieces = splitTokenString(tokenString);
		String jwtPayloadSegment = pieces[1];
		JsonParser parser = new JsonParser();
		// JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64
		// .decodeBase64(jwtPayloadSegment)));
		JsonElement payload =
		    parser.parse(StringUtils.newStringUtf8(Base64.decodeBase64(jwtPayloadSegment.getBytes())));

		return payload.toString();
	}

	/**
	 * @param tokenString
	 *          The original encoded representation of a JWT
	 * @return Three components of the JWT as an array of strings
	 */
	private String[] splitTokenString(String tokenString) {
		String[] pieces = tokenString.split(Pattern.quote("."));
		if (pieces.length != 3) {
			throw new IllegalStateException("Expected JWT to have 3 segments separated by '" + "."
			    + "', but it has " + pieces.length + " segments");
		}
		return pieces;
	}

	@Override
	public String getJWTTokenForCoupon(CouponDTO coupon) throws InvalidKeyException, SignatureException {
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty("name", ZIPLLY_INC);
		request.addProperty("description", coupon.getDescription());
		request.addProperty("price", coupon.getPrice());
		request.addProperty("currencyCode", Currency.getInstance(Locale.US).getCurrencyCode());
		// TODO do we need buyer here?
//		request.addProperty("buyer", "buyer_id:" + buyer.getAccountId());
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}
	
	private JsonToken createToken(JsonObject request) throws InvalidKeyException {
		// Current time and signing algorithm
		Calendar cal = Calendar.getInstance();
		HmacSHA256Signer signer = new HmacSHA256Signer(SELLER_ID, null, SELLER_SECRET.getBytes());

		// Configure JSON token
		JsonToken token = new JsonToken(signer);
		token.setAudience("Google");
		token.setParam("typ", "google/payments/inapp/item/v1");
		token.setIssuedAt(new Instant(cal.getTimeInMillis()));
		token.setExpiration(new Instant(cal.getTimeInMillis() + 60000L));

		JsonObject payload = token.getPayloadAsJsonObject();
		payload.add("request", request);
		return token;
	}
}
