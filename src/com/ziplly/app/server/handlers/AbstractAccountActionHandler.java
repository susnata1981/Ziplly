package com.ziplly.app.server.handlers;

import java.util.Date;
import java.util.UUID;

import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.ZipllyServerConstants;

public abstract class AbstractAccountActionHandler<T extends Action<R>,R extends Result> implements ActionHandler<T,R>{
	protected final long hoursInMillis = 60*60*2;
	protected SessionDAO sessionDao;
	protected AccountDAO accountDao;

	@Inject
	private Provider<HttpSession> httpSession;

	public AbstractAccountActionHandler(AccountDAO accountDao, SessionDAO sessionDao) {
		this.accountDao = accountDao;
		this.sessionDao = sessionDao;
	}
	
	/*
	 * 1. Make an entry into Session table
	 * 2. Create and drop cookie
	 * 3. Return UID
	 */
	protected Long doLogin(Account account) {
		// Do we need to check if session already exists. Probably not?
		Session session = new Session();
		session.setAccount(account);
		Date currTime = new Date();
		Date expireAt = new Date(currTime.getTime()+hoursInMillis);
		session.setExpireAt(expireAt);
		session.setTimeCreated(currTime);
		Long uid = UUID.randomUUID().getMostSignificantBits();
		session.setUid(uid);
		sessionDao.save(session);
		storeCookie(uid);
		return uid;
	}

	protected void storeCookie(Long uid) {
		httpSession.get().setMaxInactiveInterval((int)hoursInMillis);
		httpSession.get().setAttribute(ZipllyServerConstants.SESSION_ID, uid);
	} 
	
	@Override
	public void rollback(T arg0,
			R arg1, ExecutionContext arg2)
			throws DispatchException {
		
	}
}
