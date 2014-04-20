package com.ziplly.app.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ziplly.app.server.bli.payment.Payload;
import com.ziplly.app.server.bli.payment.Request;

@Singleton
public class ConfirmPaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JWT_TOKEN_KEY = "jwt";
	private Logger logger = Logger.getLogger(ConfirmPaymentServlet.class.getCanonicalName());
	PaymentService paymentService;

	@Inject
	public ConfirmPaymentServlet(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		System.out.println("Received request = " + req.getContentLength());
		handlePayload(req.getParameter(JWT_TOKEN_KEY), res);
	}

	private void handlePayload(String jwt, HttpServletResponse res) throws IOException {
    String orderID;
    String jwt_response = paymentService.deserialize(jwt);
    JsonParser parser = new JsonParser();
    Gson gson = new GsonBuilder().create();
    JsonArray payload = parser.parse("[" + jwt_response + "]").getAsJsonArray();
    Payload payload_1 = gson.fromJson(payload.get(0), Payload.class);
    // validate the payment request and respond back to Google
    if (payload_1.getIss().equals("Google")
            && payload_1.getAud().equals(paymentService.getIssuer())) {
        if (payload_1.getResponse() != null
                && payload_1.getResponse().getOrderId() != null) {
            orderID = payload_1.getResponse().getOrderId();
            Request req = payload_1.getRequest();
            if (req.getCurrencyCode() != null
                    && req.getPrice() != null
                    && req.getTransactionId() != null
                    && req.getBuyerId() != null) {
                
            		res.setStatus(200);
                PrintWriter writer = res.getWriter();
                writer.write(orderID);
            }
        }

    }
  }

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		logger.log(Level.INFO, "ConfirPaumentServlet called.");
		String token = req.getParameter("jwt");
		logger.log(Level.INFO, "Jwt token =" + token);
	}
}
