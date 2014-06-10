package com.ziplly.app.shared;

public enum EmailTemplate {
	WELCOME_REGISTRATION("welcome.ftl", "Welcome to Ziplly! Confirm your email with us."),
	INVITE_PEOPLE("invite.ftl", "Invitation to join ziplly.com"),
	PASSWORD_RECOVERY("password_recovery.ftl", "Reset your password for Ziplly"),
	PENDING_MESSAGE("pending_message.ftl", "Pending message"),
	SECURITY_ALERT("security_alert.ftl", "Security alert"),
	ANNOUNCEMENT("announcement.ftl", "Announcement"),
	OFFER("offer.ftl", "Offer"),
	EMAIL_VERIFICATION("email_verification.ftl", "Verify your email"), 
	COUPON_PUBLISHED("coupon_published.ftl", "A new coupon is available"),
	COUPON_PURCHASE("coupon_purchase.ftl", "Your coupon is here"),
	SUBSCRIPTION_NOTIFICATION("subscription_notification.ftl", "Subscription successful");

	private String subject;
	EmailTemplate(String filename, String subject) {
		this.setFilename(filename);
		this.setSubject(subject);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String filename;
}
