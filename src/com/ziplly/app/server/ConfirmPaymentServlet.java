package com.ziplly.app.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.PaymentService;
import com.ziplly.app.server.bli.payment.AbstractRequestHandler;
import com.ziplly.app.server.bli.payment.PayloadParser;

@Singleton
public class ConfirmPaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ConfirmPaymentServlet.class.getCanonicalName());
	private PaymentService paymentService;
	private CouponBLI couponBli;
	private PayloadParser parser;

	@Inject
	public ConfirmPaymentServlet(PayloadParser parser, PaymentService paymentService, CouponBLI couponBli) {
		this.parser = parser;
		this.couponBli = couponBli;
		this.paymentService = paymentService;
	}

	private void handlePayload(String parameter, HttpServletResponse res) {
		AbstractRequestHandler request = parser.parse(parameter);
		try {
	    request.completeTransaction();
	    res.setStatus(200);
	    PrintWriter writer = res.getWriter();
			writer.write(request.getOrderId());
    } catch (Exception ex) {
    	logger.severe(String.format("Failed to complete transaction for request %s, with exception %s", request, ex));
    }
  }

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		logger.log(Level.INFO, "ConfirPaumentServlet called");
		String token = req.getParameter("jwt");
		logger.log(Level.INFO, "Jwt token =" + token);
		try {
			handlePayload(token, res);
		} catch (Exception e) {
			logger.severe(String.format("Failed to confirm payment: %s", e));
			e.printStackTrace();
		}
	}
}
