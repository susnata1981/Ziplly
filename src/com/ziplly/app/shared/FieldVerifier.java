package com.ziplly.app.shared;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
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
	
	// TODO length check
	public static ValidationResult validatePassword(String password) {
		ValidationResult result = new ValidationResult();
		if (password == null || password.equals("")) {
			result.addError(CANT_BE_EMPTY);
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
}
