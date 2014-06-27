package com.ziplly.app.server.bli;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ziplly.app.server.bli.payment.PaymentType;
import com.ziplly.app.server.model.jpa.Coupon;
import com.ziplly.app.server.model.jpa.SubscriptionPlan;

public class PaymentServiceImpl implements PaymentService {
	private static final String CURRENCY_CODE_LABEL = "currencyCode";
	private static final String PRICE_LABEL = "price";
	private static final String DESCRIPTION_LABEL = "description";
	private static final String NAME_LABEL = "name";
	private static final String ZIPLLY_INC = "Ziplly Inc.";
	private static final String COUPON_ID_LABEL = "couponId";
	private static final String BUYER_ID_LABEL = "buyerId";
	private static final String COUPON_ORDER_ID_LABEL = "couponOrderId";
	private static final String PAYMENT_TYPE = "paymentType";
	private static final String MERCHANT_ID_LABEL = "sellerId";
	private static final String SUBSCRIPTION_ID_LABEL = "subscriptionId";
	private static final String SUBSCRIPTION_TRIAL_PERIOD_KEY = "app.feature.subscriptionTrailPeriod";
  private static final String START_TIME_KEY = "startTime";
  private static final String FREQUENCY_VALUE = "monthly";
  private static final String FREQUENCY_KEY = "frequency";
  private static final String SELLER_DATA_KEY = "sellerData";
  public static final String ACCOUNT_ID_KEY = "accountId";
  public static final String RECURRENCE_KEY = "recurrence";
  public static final String INITIAL_PAYMENT_KEY = "initialPayment";
  public static final String AUD_GOOGLE = "Google";
  public static final String SUBSCRIPTION_ID_KEY = "subscriptionId";
  
  public static final String KEY_SEPARATOR = ":";
  public static final String VALUE_SEPARATOR = ",";
  
	private final String sellerSecret;
	private final String sellerId;

	private Logger logger = Logger.getLogger(PaymentServiceImpl.class.getName());
	
	@Inject
	public PaymentServiceImpl(
			@Named("seller_id") String sellerId, 
			@Named("seller_secret") String sellerSecret) {
		this.sellerId = sellerId;
		this.sellerSecret = sellerSecret;
		logger.info(String.format("PAYMENT SERVICE using id %s, secret %s", sellerId, sellerSecret));
  }

	@Deprecated
	@Override
	public String generateSubscriptionToken(Long sellerId, Long subscriptionId, BigDecimal amount) throws InvalidKeyException, SignatureException {
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty(NAME_LABEL, ZIPLLY_INC);
		request.addProperty(DESCRIPTION_LABEL, "Subscription service fee");
		request.addProperty(PRICE_LABEL, amount);
		request.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
		request.addProperty(MERCHANT_ID_LABEL, sellerId);
		request.addProperty(PAYMENT_TYPE, PaymentType.SUBSCRIPTION.name());
		request.addProperty(SUBSCRIPTION_ID_LABEL, subscriptionId);
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}
	
	@Override
	public String deserialize(String tokenString) {
		String[] pieces = splitTokenString(tokenString);
		String jwtPayloadSegment = pieces[1];
		JsonParser parser = new JsonParser();
		JsonElement payload =
		    parser.parse(StringUtils.newStringUtf8(Base64.decodeBase64(jwtPayloadSegment.getBytes())));

		return payload.toString();
	}

	@Override
	public String generateTokenForCoupon(
			Long orderId, 
			Coupon coupon, 
			Long buyerAccountId) throws InvalidKeyException, SignatureException {
		
		// Configure request object
		JsonObject request = new JsonObject();
		request.addProperty(NAME_LABEL, ZIPLLY_INC);
		request.addProperty(DESCRIPTION_LABEL, coupon.getDescription());
		request.addProperty(PRICE_LABEL, coupon.getDiscountedPrice());
		request.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
		request.addProperty(COUPON_ID_LABEL, coupon.getCouponId());
		request.addProperty(BUYER_ID_LABEL, buyerAccountId);
		request.addProperty(COUPON_ORDER_ID_LABEL, orderId);
		request.addProperty(PAYMENT_TYPE, PaymentType.COUPON.name());
		JsonToken token = createToken(request);
		return token.serializeAndSign();
	}
	
	@Override
  public String generateJWTTokenForSubscription(
      SubscriptionPlan plan, 
      Long buyerAccountId) throws InvalidKeyException, SignatureException {
    
    JsonObject initialPayment = new JsonObject();
    initialPayment.addProperty(PRICE_LABEL, "0");
    initialPayment.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
    initialPayment.addProperty("paymentType", "prorated");
    
    DateTime now = new DateTime();
    JsonObject recurrence = new JsonObject();
    recurrence.addProperty(PRICE_LABEL, plan.getFee().toString());
    recurrence.addProperty(CURRENCY_CODE_LABEL, Currency.getInstance(Locale.US).getCurrencyCode());
    recurrence.addProperty(START_TIME_KEY, getSubscriptionStartTime(now));
    recurrence.addProperty(FREQUENCY_KEY, FREQUENCY_VALUE);
    
    // Configure request object
    JsonObject request = new JsonObject();
    request.addProperty(NAME_LABEL, plan.getName());
    request.addProperty(DESCRIPTION_LABEL, plan.getDescription());
    request.addProperty(SELLER_DATA_KEY, getSellerData(plan, buyerAccountId));
    request.addProperty(PAYMENT_TYPE, PaymentType.SUBSCRIPTION.name());
    request.add(INITIAL_PAYMENT_KEY, initialPayment);
    request.add(RECURRENCE_KEY, recurrence);
    
    JsonToken token = createToken(request, now);
    return token.serializeAndSign();
  }
	
	private String getSellerData(SubscriptionPlan plan, Long buyerAccountId) {
	  return ACCOUNT_ID_KEY + KEY_SEPARATOR + buyerAccountId 
	      + VALUE_SEPARATOR + SUBSCRIPTION_ID_KEY + KEY_SEPARATOR + plan.getSubscriptionId();  
  }

  long getSubscriptionStartTime(DateTime now) {
	  // In days
	  String trialPeriod = System.getProperty(SUBSCRIPTION_TRIAL_PERIOD_KEY, "60");
	  DateTime plusDays = now.plusDays(Integer.parseInt(trialPeriod));
	  return new Instant(plusDays).getMillis();
  }

  private JsonToken createToken(JsonObject request, DateTime now) throws InvalidKeyException {
		// Current time and signing algorithm
		HmacSHA256Signer signer = new HmacSHA256Signer(sellerId, null, sellerSecret.getBytes());

		// Configure JSON token
		JsonToken token = new JsonToken(signer);
		token.setAudience(AUD_GOOGLE);
    token.setParam("typ", "google/payments/inapp/subscription/v1");
		Instant currInstant = new Instant(now.getMillis());
		token.setIssuedAt(currInstant);
		token.setExpiration(currInstant.plus(60 * 1000 * 1000L));

		JsonObject payload = token.getPayloadAsJsonObject();
		payload.add("request", request);
		return token;
	}

  private JsonToken createToken(JsonObject request) throws InvalidKeyException {
    // Current time and signing algorithm
    Calendar cal = Calendar.getInstance();
    HmacSHA256Signer signer = new HmacSHA256Signer(sellerId, null, sellerSecret.getBytes());
    // Configure JSON token
    JsonToken token = new JsonToken(signer);
    token.setAudience(AUD_GOOGLE);
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
	
	/**
	 * @param tokenString original encoded representation of a JWT
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
}
