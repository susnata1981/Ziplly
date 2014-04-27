package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;

public class RegisterAccountActionHandler extends
    AbstractAccountActionHandler<RegisterAccountAction, RegisterAccountResult> {

	@Inject
	public RegisterAccountActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public RegisterAccountResult
	    doExecute(RegisterAccountAction action, ExecutionContext ec) throws DispatchException {

		if (action == null || action.getAccount() == null) {
			throw new IllegalArgumentException("Invalid argument to RegisterAccountResult");
		}
		
		RegisterAccountResult result = new RegisterAccountResult();
		AccountDTO accountDto = action.getAccount();
		Account account = EntityUtil.convert(accountDto);
		try {
			boolean saveImage = false;
			if (accountDto instanceof PersonalAccountDTO) {
				saveImage = ((PersonalAccountDTO) accountDto).getFacebookRegistration();
			}

			AccountDTO newAccount = accountBli.register(account, saveImage);
			result.setAccount(newAccount);
			result.setUid(accountDto.getUid());
			return result;
		} catch (AccountExistsException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalException();
		}
	}

	@Override
	public Class<RegisterAccountAction> getActionType() {
		return RegisterAccountAction.class;
	}
}
