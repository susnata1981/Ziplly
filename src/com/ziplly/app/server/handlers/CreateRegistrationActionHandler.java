package com.ziplly.app.server.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.AccountRegistrationDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountRegistration;
import com.ziplly.app.model.AccountRegistration.AccountRegistrationStatus;
import com.ziplly.app.model.Role;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.resources.IResource;
import com.ziplly.app.server.resources.Resource;
import com.ziplly.app.shared.CreateRegistrationAction;
import com.ziplly.app.shared.CreateRegistrationResult;

public class CreateRegistrationActionHandler extends AbstractAccountActionHandler<CreateRegistrationAction, CreateRegistrationResult>
	implements IResource {

	private AccountRegistrationDAO registrationDao;

	@Inject
	public CreateRegistrationActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, AccountRegistrationDAO registrationDao) {
		super(accountDao, sessionDao, accountBli);
		this.registrationDao = registrationDao;
	}
	
	@Override
	public CreateRegistrationResult execute(CreateRegistrationAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null || action.getEmail() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		Account account = session.getAccount();
		if (account.getRole() != Role.ADMINISTRATOR) {
			throw new IllegalAccessError();
		}
		
		long code = generateRegistrationCode(action.getEmail());
		AccountRegistration ar = new AccountRegistration();
		ar.setEmail(action.getEmail());
		ar.setAccountType(action.getType());
		ar.setBusinessType(action.getBusinessType());
		ar.setCode(code);
		ar.setStatus(AccountRegistrationStatus.ACTIVE);
		registrationDao.create(ar);
		try {
			String registrationLink = URLEncoder.encode("code="+code+"email="+action.getEmail(),"utf-8");
			// TODO send email
			return new CreateRegistrationResult(registrationLink);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("Couldn't create registration link");
		}
	}

	private long generateRegistrationCode(String email) {
		return email.hashCode();
	}

	@Override
	public Class<CreateRegistrationAction> getActionType() {
		return CreateRegistrationAction.class;
	}

	@Override
	public int getResourceId() {
		return Resource.CREATE_REGISTRATION_ACTION;
	}
}
