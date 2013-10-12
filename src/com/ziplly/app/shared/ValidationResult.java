package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	ArrayList<ErrorMessage> errors = new ArrayList<ErrorMessage>();

	public ValidationResult() {
	}

	public void addError(String error) {
		if (error != null) {
			errors.add(new ErrorMessage(error));
		}
	}

	public boolean isValid() {
		return errors.size() == 0;
	}

	public List<ErrorMessage> getErrors() {
		return errors;
	}

	public static class ErrorMessage {
		String error;

		public ErrorMessage(String error) {
			this.error = error;
		}
		
		public String getErrorMessage() {
			return error;
		}
	}
}
