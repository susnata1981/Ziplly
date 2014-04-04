package com.ziplly.app.server.handlers;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.server.ZipllyServerConstants;

public abstract class AbstractAccountActionHandler<T extends Action<R>, R extends Result> 
    extends AbstractSessionAwareActionHandler<T, R> {
	
	protected final long hoursInMillis = 60 * 60 * 2;
	protected SessionDAO sessionDao;
	protected AccountDAO accountDao;

	@Inject
	protected Provider<HttpSession> httpSession;
	protected AccountBLI accountBli;
	protected Session session;

	public AbstractAccountActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		
		super(entityManagerProvider);
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
		this.accountBli = accountBli;
	}

	protected boolean validateSession() throws NeedsLoginException {
		Long existingUid = (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
		if (existingUid != null) {
			try {
				this.session = sessionDao.findSessionByUid(existingUid);
				if (isValidSession(session)) {
					return true;
				}
			} catch (NumberFormatException e) {
				throw e;
			} catch (NotFoundException e) {
			}
		}
		throw new NeedsLoginException();
	}

	protected boolean isValidSession(Session session) {
		if (session == null) {
			return false;
		}
		return session.getExpireAt().after(new Date());
	}

	protected Long getUidFromCookie() {
		return (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
	}

	@Override
	public void rollback(T arg0, R arg1, ExecutionContext arg2) throws DispatchException {
	}
}
