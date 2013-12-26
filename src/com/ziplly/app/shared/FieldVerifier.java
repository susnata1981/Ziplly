package com.ziplly.app.shared;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> package because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class FieldVerifier {
	private static final String PASSWORD_MISMATCH_ERROR = "Password & Confirm Password doesn't match";
	private static final String CANT_BE_EMPTY = "Can't be empty";
	private static final String INVALID_ZIP = "Invalid zip";
	private static final String INVALID_EMAIL = "Invalid email";

	public static ValidationResult validateName(String name) {
		ValidationResult result = new ValidationResult();
		if (name == null || "".equals(name)) {
			result.addError(CANT_BE_EMPTY);
		}
		return result;
	}
	
	private static final RegExp emailPattern = RegExp.compile("\\w+@[a-z]+\\.[a-z]{2,3}");
	private static final RegExp zipPattern = RegExp.compile("(\\d+){3,5}");
	private static final int MAX_TWEET_LENGTH = 256;
	private static final int MAX_COMMENT_LENGTH = 256;
	private static final String TWEET_TOO_LONG_ERROR = "Tweet can't be more than 256 characters.";
	private static final String INVALID_EMAIL_LIST = "Invalid emails";
	private static final String COMMA_SEPARATOR = ",";
	private static final int MAX_MESSAGE_LENGTH = 2048;
	private static final String MESSAGE_TOO_LONG_ERROR = "Message can't be more than 2048 characters.";
	private static final int MIN_PASSWORD_LENGTH = 4;
	private static final int MAX_PASSWORD_LENGTH = 16;
	private static final String MINIMUN_PASSWORD_LENGTH_ERROR = "Password should be atleast "+MIN_PASSWORD_LENGTH+" characters.";
	private static final String MAX_PASSWORD_LENGTH_ERROR = "Password can't be more than "+MAX_PASSWORD_LENGTH+" characters.";
	
	public static ValidationResult validateEmail(String email) {
		ValidationResult result = new ValidationResult();
		if (email == null || email.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}

		MatchResult matcher = emailPattern.exec(email);
		if (matcher == null) {
			result.addError(INVALID_EMAIL);
		}
		return result;
	}
	
	public static ValidationResult validateTweet(String tweet) {
		ValidationResult result = new ValidationResult();
		if (tweet == null || tweet.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}
		
		tweet = SafeHtmlUtils.htmlEscape(tweet);
		if (tweet.length() > MAX_TWEET_LENGTH) {
			result.addError(TWEET_TOO_LONG_ERROR);
		}
		return result;
	}
	
	/*
	 * Special purpose: to be used in TweetBox only
	 */
	public static ValidationResult validateTweetLength(String tweet) {
		ValidationResult result = new ValidationResult();
		if (tweet == null || tweet.equals("")) {
			return result;
		}
		
		tweet = SafeHtmlUtils.htmlEscape(tweet);
		if (tweet.length() > MAX_TWEET_LENGTH) {
			result.addError(TWEET_TOO_LONG_ERROR);
		}
		return result;
	}
	
	// TODO length check
	public static ValidationResult validatePassword(String password) {
		ValidationResult result = new ValidationResult();
		if (password == null || password.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}
		
		String p = sanitize(password);
		if (p.length() < MIN_PASSWORD_LENGTH) {
			result.addError(MINIMUN_PASSWORD_LENGTH_ERROR);
		}
		
		if (p.length() > MAX_PASSWORD_LENGTH) {
			result.addError(MAX_PASSWORD_LENGTH_ERROR);
		}
		return result;
	}
	
	public static ValidationResult validateZip(String zip) {
		ValidationResult result = new ValidationResult();
		if (zip == null || zip.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}
		
		MatchResult matcher = zipPattern.exec(zip);
		if (matcher == null) {
			result.addError(INVALID_ZIP);
		}
		return result;
	}
	
	public static String getEscapedText(String input) {
		if (input == null) {
			throw new IllegalArgumentException();
		}
		
		return SafeHtmlUtils.htmlEscape(input.toLowerCase().trim());
	}
	
	public static String sanitize(String input) {
		if (input == null) {
			return null;
		}
		return SafeHtmlUtils.htmlEscape(input.toLowerCase().trim());
	}

	public static ValidationResult validateEmailList(String emailList) {
		ValidationResult result = new ValidationResult();
		if (emailList == null || emailList.equals("")) {
			result.addError(CANT_BE_EMPTY);
			return result;
		}
		
		String[] emails = emailList.split(COMMA_SEPARATOR);
		for(String email : emails) {
			ValidationResult validation = validateEmail(email);
			if (!validation.isValid()) {
				result.addError(INVALID_EMAIL_LIST);
				return result;
			}
		}
		return result;
	}

	public static ValidationResult validateComment(String comment) {
		ValidationResult result = new ValidationResult();
		if (comment == null || comment.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}
		
		comment = SafeHtmlUtils.htmlEscape(comment);
		if (comment.length() > MAX_COMMENT_LENGTH) {
			result.addError(TWEET_TOO_LONG_ERROR);
		}
		return result;
	}

	public static ValidationResult validateMessage(String message) {
		ValidationResult result = new ValidationResult();
		if (message == null || message.equals("")) {
			result.addError(CANT_BE_EMPTY);
		}
		
		message = SafeHtmlUtils.htmlEscape(message);
		if (message.length() > MAX_MESSAGE_LENGTH) {
			result.addError(MESSAGE_TOO_LONG_ERROR);
		}
		return result;
	}
}
