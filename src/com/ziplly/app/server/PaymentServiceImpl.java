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
import com.google.inject.Inject;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.server.guice.ServiceModule.SellerIdentifier;
import com.ziplly.app.server.guice.ServiceModule.SellerSecret;

public class PaymentServiceImpl implements PaymentService {
	private static final String CURRENCY_CODE_LABEL = "currencyCode";
	private static final String PRICE_LABEL = "price";
	private static final String DESCRIPTION_LABEL = "description";
	private static final String NAME_LABEL = "name";
//	private static final String SELLER_ID = "15732117996604841954";
//	private static final String SELLER_SECRET = "zYfFCJLbvBgnVsAxa0wMpQ";
	private static final String ZIPLLY_INC = "Ziplly Inc.";
	private static final String COUPON_ID_LABEL = "couponId";
	private static final String BUYER_ID_LABEL = "buyerId";
	private static final String TRANSACTION_ID_LABEL = "transactionId";
	
	private final String sellerSecret;
	private final String sellerId;

	@Inject
	public PaymentServiceImpl(@SellerIdentifier String sellerId, @SellerSecret String sellerSecret) {
		this.sellerId = sellerId;
		this.sellerSecret = sellerSecret;
  }
	
	@Deprecated
	@Override
	public String getJWT(Long sellerId, Double amount) throws InvalidKeyException, SignatureException {
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty(NAME_LABEL, "ziplly marketing service");
		request.addProperty(DESCRIPTION_LABEL, "ziplly marketing service fee");
		request.addProperty(PRICE_LABEL, amount);
		request.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
		request.addProperty("sellerData", "seller_id:" + sellerId);
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}

	@Override
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
	public String getJWTTokenForCoupon(Long transactionId, CouponDTO coupon, Long buyerAccountId) throws InvalidKeyException, SignatureException {
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty(NAME_LABEL, ZIPLLY_INC);
		request.addProperty(DESCRIPTION_LABEL, coupon.getDescription());
		request.addProperty(PRICE_LABEL, coupon.getPrice());
		request.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
		request.addProperty(COUPON_ID_LABEL, coupon.getCouponId());
		request.addProperty(BUYER_ID_LABEL, buyerAccountId);
		request.addProperty(TRANSACTION_ID_LABEL, transactionId);
//		request.addProperty("buyer", "buyer_id:" + buyer.getAccountId());
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}
	
	private JsonToken createToken(JsonObject request) throws InvalidKeyException {
		// Current time and signing algorithm
		Calendar cal = Calendar.getInstance();
		HmacSHA256Signer signer = new HmacSHA256Signer(sellerId, null, sellerSecret.getBytes());

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

	@Override
  public String getIssuer() {
		return sellerId;
  }
}
