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

@Singleton
public class ConfirmPaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ConfirmPaymentServlet.class.getCanonicalName());
	PaymentService paymentService;
	
	public ConfirmPaymentServlet() {
	}
	
	@Inject
	public ConfirmPaymentServlet(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	
	@Override 
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PrintWriter writer = res.getWriter();
		writer.println("ConfirmPaymentServlet...");
		writer.close();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		logger.log(Level.INFO, "ConfirPaumentServlet called.");
		String token = req.getParameter("jwt");
		logger.log(Level.INFO, "Jwt token ="+token);
	}
}
