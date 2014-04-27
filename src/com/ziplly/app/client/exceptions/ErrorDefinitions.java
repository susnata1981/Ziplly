package com.ziplly.app.client.exceptions;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.ziplly.app.client.resource.DynamicStringDefinitions;
import com.ziplly.app.client.resource.StringDefinitions;

import net.customware.gwt.dispatch.shared.DispatchException;

public class ErrorDefinitions {
	private final static StringDefinitions stringDefinitions = GWT.create(StringDefinitions.class);
	private final static DynamicStringDefinitions dynamicStringDefinitions = GWT.create(DynamicStringDefinitions.class);
	
	private static Map<Class<? extends DispatchException>, ErrorDefinition<? extends DispatchException>> errorMap = 
			new HashMap<Class<? extends DispatchException>, ErrorDefinition<? extends DispatchException>>();
	
	public static final ErrorDefinition<?> accessError = new ErrorDefinition<AccessException>(
			AccessException.class, 
			ErrorCodes.AccessError, 
			stringDefinitions.accessError(), 
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> duplicateError = new ErrorDefinition<DuplicateException>(
			DuplicateException.class,
			ErrorCodes.DuplicateError,
			stringDefinitions.duplicateError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> accountAlreadyExistsError = new ErrorDefinition<AccountExistsException>(
			AccountExistsException.class,
			ErrorCodes.AccountAlreadyExistsError,
			stringDefinitions.accountAlreadyExists(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> accountNotActiveError = new ErrorDefinition<AccountNotActiveException>(
			AccountNotActiveException.class,
			ErrorCodes.AccountNotActiveError,
			stringDefinitions.accountNotActive(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> couponAlreadyUsedError = new ErrorDefinition<CouponAlreadyUsedException>(
			CouponAlreadyUsedException.class,
			ErrorCodes.CouponAlreadyUsedError,
			stringDefinitions.couponAlreadyUsed(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> couponCampaignEndedError = new ErrorDefinition<CouponCampaignEndedException>(
			CouponCampaignEndedException.class,
			ErrorCodes.CouponCampaignEndedError,
			stringDefinitions.couponCampaignEndedError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> internalError = new ErrorDefinition<InternalException>(
			InternalException.class,
			ErrorCodes.InternalError,
			stringDefinitions.internalError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> invalidCouponError = new ErrorDefinition<InvalidCouponException>(
			InvalidCouponException.class,
			ErrorCodes.InvalidCouponError,
			stringDefinitions.invalidCouponError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> invalidCredentialsError = new ErrorDefinition<InvalidCredentialsException>(
			InvalidCredentialsException.class,
			ErrorCodes.InvalidCredentialsError,
			stringDefinitions.invalidCredentialsError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> needsLoginError = new ErrorDefinition<NeedsLoginException>(
			NeedsLoginException.class,
			ErrorCodes.NeedsLoginError,
			stringDefinitions.needsLoginError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> notFoundError = new ErrorDefinition<NotFoundException>(
			NotFoundException.class,
			ErrorCodes.NotFoundError,
			stringDefinitions.notFoundError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> oauthError = new ErrorDefinition<OAuthException>(
			OAuthException.class,
			ErrorCodes.OAuthError,
			stringDefinitions.authError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> soldoutError = new ErrorDefinition<CouponSoldOutException>(
			CouponSoldOutException.class,
			ErrorCodes.SoldoutError,
			stringDefinitions.couponSoldOutError(),
			AlertType.ERROR);
	
	public static final ErrorDefinition<?> usageLimitExceededError = new ErrorDefinition<UsageLimitExceededException>(
			UsageLimitExceededException.class,
			ErrorCodes.UsageLimitExceededError,
			stringDefinitions.usageLimitExceededError(),
			AlertType.ERROR);
	
	static {
		errorMap.put(accessError.getException(), accessError);
		errorMap.put(duplicateError.getException(), duplicateError);
		errorMap.put(accountAlreadyExistsError.getException(), accountAlreadyExistsError);
		errorMap.put(accountNotActiveError.getException(), accountNotActiveError);
		errorMap.put(couponAlreadyUsedError.getException(), couponAlreadyUsedError);
		errorMap.put(couponCampaignEndedError.getException(), couponCampaignEndedError);
		errorMap.put(internalError.getException(), internalError);
		errorMap.put(invalidCouponError.getException(), invalidCouponError);
		errorMap.put(invalidCredentialsError.getException(), invalidCredentialsError);
		errorMap.put(needsLoginError.getException(), needsLoginError);
		errorMap.put(notFoundError.getException(), notFoundError);
		errorMap.put(notFoundError.getException(), notFoundError);
		errorMap.put(oauthError.getException(), oauthError);
		errorMap.put(soldoutError.getException(), soldoutError);
		errorMap.put(usageLimitExceededError.getException(), usageLimitExceededError);
	}
	
	public static <T extends DispatchException> ErrorDefinition<T> getErrorDefinition(Class<?> clazz) {
		return (ErrorDefinition<T>) errorMap.get(clazz);
	}
	
	public static class ErrorDefinition<T extends DispatchException> {
		private Class<T> exception;
		private ErrorCodes code;
		private String errorMessage;
		private AlertType type;
		
		public ErrorDefinition(Class<T> exception, ErrorCodes code, String errorMessage, AlertType type) {
			this.exception = exception;
			this.code = code;
			this.errorMessage = errorMessage;
			this.type = type;
    }

		public String getErrorMessage() {
	    return errorMessage;
    }
		
		public Class<T> getException() {
			return exception;
		}

		public ErrorCodes getCode() {
			return code;
		}

		public AlertType getType() {
			return type;
		}
	}
}
