package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public class ValidateLoginActionHandler extends AbstractSessionAwareActionHandler<ValidateLoginAction, ValidateLoginResult> {
	private AccountBLI accountBli;

	@Inject
	public ValidateLoginActionHandler(
			Provider<EntityManager> entityManagerProvider,
	    AccountBLI accountBli) {
		super(entityManagerProvider);
		this.accountBli = accountBli;
	}

	@Override
	public ValidateLoginResult doExecute(ValidateLoginAction action, ExecutionContext ec) throws DispatchException {

		if (action == null) {
			throw new IllegalArgumentException("Invalid argument to ValidateLoginActionHandler");
		}

		AccountDTO account = accountBli.validateLogin(action.getEmail(), action.getPassword());
		return new ValidateLoginResult(account);
	}

	@Override
	public Class<ValidateLoginAction> getActionType() {
		return ValidateLoginAction.class;
	}
}
