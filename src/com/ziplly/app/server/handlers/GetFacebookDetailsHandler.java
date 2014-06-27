package com.ziplly.app.server.handlers;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookDetailsResult;

public class GetFacebookDetailsHandler extends
    AbstractAccountActionHandler<GetFacebookDetailsAction, GetFacebookDetailsResult> {

	Logger logger = Logger.getLogger(GetFacebookDetailsHandler.class.getCanonicalName());

	@Inject
	public GetFacebookDetailsHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public GetFacebookDetailsResult
	    doExecute(GetFacebookDetailsAction input, ExecutionContext context) throws DispatchException {

		if (input == null || input.getCode() == null) {
			throw new IllegalArgumentException();
		}
		logger.info(String.format("Initiating token exchange %s", input.getCode()));
		Account account = accountBli.getFacebookDetails(input.getCode());
		logger.info(String.format("Received account back from fb %s", account));
		return new GetFacebookDetailsResult(EntityUtil.convert(account));
	}

	@Override
	public Class<GetFacebookDetailsAction> getActionType() {
		return GetFacebookDetailsAction.class;
	}
}
