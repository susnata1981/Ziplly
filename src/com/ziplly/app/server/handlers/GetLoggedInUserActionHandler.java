package com.ziplly.app.server.handlers;

import java.util.Date;

import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public class GetLoggedInUserActionHandler implements ActionHandler<GetLoggedInUserAction, GetLoggedInUserResult> {
	private SessionDAO sessionDao;
	@Inject
	private Provider<HttpSession> httpSession;
	
	@Inject
	public GetLoggedInUserActionHandler(
			SessionDAO sessionDao) {
		this.sessionDao = sessionDao;
	}
	
	@Override
	public GetLoggedInUserResult execute(GetLoggedInUserAction action,
			ExecutionContext ec) throws DispatchException {
		Long uid = (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
		GetLoggedInUserResult result = new GetLoggedInUserResult();
		
		if (uid == null) {
			return null;
		}
		
		Session session = null;
		try {
			session = sessionDao.findSessionByUid(uid);
		} catch (NotFoundException nfe) {
			return result;
		}
		
		if (isValidSession(session)) {
			Account account = session.getAccount();
			AccountDTO loggedInAccount = new AccountDTO(account);
			loggedInAccount.setUid(uid);
			result.setAccount(loggedInAccount);
			return result;
		}
		return result;
	}

	private boolean isValidSession(Session session) {
		if (session == null) {
			return false;
		}
		
		return session.getExpireAt().before(new Date());
	}

	@Override
	public Class<GetLoggedInUserAction> getActionType() {
		return GetLoggedInUserAction.class;
	}

	@Override
	public void rollback(GetLoggedInUserAction arg0,
			GetLoggedInUserResult arg1, ExecutionContext arg2)
			throws DispatchException {
		// TODO Auto-generated method stub
		
	}

}
