package com.ziplly.app.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.inject.Singleton;
import com.ziplly.app.shared.EmailTemplate;

@Singleton
public class EmailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private EmailService emailService;
	
	public EmailServlet() {
		this.emailService = new EmailServiceImpl();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		System.out.println("Received request with email:"+req.getParameter("recipientEmail"));
		String recipientEmail = req.getParameter("recipientEmail");
		String recipientName = req.getParameter("recipientName");
		String emailTemplateName = req.getParameter("emailTemplateId");
		EmailTemplate template = EmailTemplate.valueOf(emailTemplateName);
		emailService.sendEmail(recipientName, recipientEmail, template);
		res.setStatus(HttpStatus.SC_OK);
		res.getWriter().println("");
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(HttpStatus.SC_OK);
		res.setHeader("Content-Type", "text/html");
		res.getWriter().println("");
	}
}
