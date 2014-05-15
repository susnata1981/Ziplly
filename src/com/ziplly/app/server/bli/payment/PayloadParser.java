package com.ziplly.app.server.bli.payment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.PaymentService;
import com.ziplly.app.server.bli.SubscriptionBLI;

public class PayloadParser {
	private PaymentService paymentService;
	private Logger logger = Logger.getLogger(PayloadParser.class.getName());
	private SubscriptionBLI subscriptionBli;
	private CouponBLI couponBli;
	
	@Inject
	public PayloadParser(PaymentService paymentService, SubscriptionBLI subscriptionBli, CouponBLI couponBli) {
		this.paymentService = paymentService;
		this.couponBli = couponBli;
		this.subscriptionBli = subscriptionBli;
  }
	
	public BaseRequest parse(String jwt_response) {
		checkNotNull(jwt_response);
		String jwt = paymentService.deserialize(jwt_response);
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().create();
		JsonArray payload = parser.parse("[" + jwt + "]").getAsJsonArray();
		Payload payload_1 = gson.fromJson(payload.get(0), Payload.class);
		String orderID;
		
		// validate the payment request and respond back to Google
		if (payload_1.getIss().equals("Google")
		    && payload_1.getAud().equals(paymentService.getIssuer())) {
			
			if (payload_1.getResponse() != null && payload_1.getResponse().getOrderId() != null) {
				orderID = payload_1.getResponse().getOrderId();
				logger.info(String.format("OrderID %s", orderID));
				
				BaseRequest response = null;
				Request req = payload_1.getRequest();
				if (req.getPaymentType() == PaymentType.COUPON) {
					response = parseCouponFields(req);
				} else if (req.getPaymentType() == PaymentType.SUBCRIPTION) {
					response = parseSubscriptionField(req);
				}
				
				response.setOrderId(orderID);
				return response;
			}
		}
		
		throw new RuntimeException("Bad jwt string");
	}

	private SubscriptionRequest parseSubscriptionField(Request req) {
		checkNotNull(req.getSellerId());
		checkNotNull(req.getSubscriptionId());

		SubscriptionRequest sr = new SubscriptionRequest(subscriptionBli);
		sr.setSellerId(Long.parseLong(req.getSellerId()));
		sr.setSubscriptionId(Long.parseLong(req.getSubscriptionId()));
		return sr;
  }

	private CouponRequest parseCouponFields(Request req) {
		CouponRequest cr = new CouponRequest(couponBli);
		parseCommonFields(req, cr);
		checkNotNull(req.getPurchasedCouponId());
		checkNotNull(req.getBuyerId());
		checkNotNull(req.getCouponId());
		
		cr.setBuyerId(Long.parseLong(req.getBuyerId()));
		cr.setCouponId(Long.parseLong(req.getCouponId()));
		cr.setPurchasedCouponId(Long.parseLong(req.getPurchasedCouponId()));
		return cr;
  }

	private void parseCommonFields(Request req, BaseRequest res) {
		checkNotNull(req.getName());
		checkNotNull(req.getDescription());
		checkNotNull(req.getPrice());
		
	  res.setCurrencyCode(req.getCurrencyCode());
	  res.setDescription(req.getDescription());
	  res.setName(req.getName());
	  res.setPrice(req.getPrice());
  }
}
