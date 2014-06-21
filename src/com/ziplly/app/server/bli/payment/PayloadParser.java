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
import com.ziplly.app.server.bli.PaymentServiceImpl;
import com.ziplly.app.server.bli.SubscriptionBLI;

public class PayloadParser {
  private PaymentService paymentService;
  private Logger logger = Logger.getLogger(PayloadParser.class.getName());
  private SubscriptionBLI subscriptionBli;
  private CouponBLI couponBli;

  @Inject
  public PayloadParser(PaymentService paymentService,
      SubscriptionBLI subscriptionBli,
      CouponBLI couponBli) {
    this.paymentService = paymentService;
    this.couponBli = couponBli;
    this.subscriptionBli = subscriptionBli;
  }

  public AbstractRequestHandler parse(String jwt_response) {
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
        // Cancellation
        if (payload_1.getResponse().getStatusCode() != null) {
          logger.info(String.format("Received subscription cancellation request for orderId %s", orderID));
          AbstractRequestHandler response = new SubscriptionCancellationRequestHandler(subscriptionBli);
          response.setOrderId(orderID);
          return response;
        }
        // Purchase completion
        else {
          logger.info(String.format("Received postback for OrderID %s", orderID));
          AbstractRequestHandler response = null;
          Request req = payload_1.getRequest();
          if (req.getPaymentType() == PaymentType.COUPON) {
            logger.info(String.format("Received coupon completion notificaiton for orderId %s", orderID));
            response = parseCouponFields(req);
          } else if (req.getPaymentType() == PaymentType.SUBSCRIPTION) {
            logger.info(String.format("Received subscription completion notificaiton for orderId %s", orderID));
            response = parseSubscriptionField(req);
          }
          response.setOrderId(orderID);
          return response;
        }
      }
    }

    throw new RuntimeException("Bad jwt string");
  }

  private SubscriptionRequestHandler parseSubscriptionField(Request req) {
    SubscriptionRequestHandler sr = new SubscriptionRequestHandler(subscriptionBli);
    parseSubscriptionSellerData(req.getSellerData(), sr);
    return sr;
  }

  private void parseSubscriptionSellerData(String sellerData, SubscriptionRequestHandler sr) {
    String[] split = sellerData.split(PaymentServiceImpl.VALUE_SEPARATOR);
    if (split.length != 2) {
      throw new RuntimeException("Error parsing subscription postback");
    }

    sr.setSellerId(Long.parseLong(getValue(split[0])));
    sr.setSubscriptionId(Long.parseLong(getValue(split[1])));
  }

  // accountId:1
  private String getValue(String pair) {
    return pair.split(PaymentServiceImpl.KEY_SEPARATOR)[1];
  }

  private CouponRequestHandler parseCouponFields(Request req) {
    CouponRequestHandler cr = new CouponRequestHandler(couponBli);
    parseCommonFields(req, cr);
    checkNotNull(req.getCouponOrderId());
    checkNotNull(req.getBuyerId());
    checkNotNull(req.getCouponId());

    cr.setBuyerId(Long.parseLong(req.getBuyerId()));
    cr.setCouponId(Long.parseLong(req.getCouponId()));
    cr.setCouponOrderId(Long.parseLong(req.getCouponOrderId()));
    return cr;
  }

  private void parseCommonFields(Request req, AbstractRequestHandler res) {
    checkNotNull(req.getName());
    checkNotNull(req.getDescription());
    checkNotNull(req.getPrice());

    res.setCurrencyCode(req.getCurrencyCode());
    res.setDescription(req.getDescription());
    res.setName(req.getName());
    res.setPrice(req.getPrice());
  }
}
