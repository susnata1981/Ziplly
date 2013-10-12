package com.ziplly.app.server.handlers;

import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;

public class LogoutActionHandler implements ActionHandler<LogoutAction, LogoutResult>{
	private SessionDAO sessionDao;
	private Provider<HttpSession> httpSession;
	private Logger logger = Logger.getLogger(LogoutActionHandler.class.getCanonicalName());
	@Inject
	public LogoutActionHandler(SessionDAO sessionDao, Provider<HttpSession> httpSession) {
		this.sessionDao = sessionDao;
		this.httpSession = httpSession;
	}
	
	@Override
	public LogoutResult execute(LogoutAction action, ExecutionContext ec)
			throws DispatchException {
		
		if (action == null || action.getUid() == null) {
			throw new IllegalArgumentException();
		}
		
		Long uidInCookie = getUidFromCookie();
		Long uidInRequest = action.getUid();
		if (uidInCookie == null || !uidInCookie.equals(uidInRequest)) {
			throw new IllegalAccessError();
		}
		
		LogoutResult result = new LogoutResult();
		Session session = sessionDao.findSessionByUid(uidInRequest);
		if (session == null) {
			logger.log(Level.WARN, String.format("Session %l exists in cookie but not in session table", uidInCookie));
			return result;
		}
		
		sessionDao.removeByUid(session.getUid());
		return result;
	}

	Long getUidFromCookie() {
		return (Long) httpSession.get().getAttribute(ZipllyServerConstants.SESSION_ID);
	}
	
	@Override
	public Class<LogoutAction> getActionType() {
		return LogoutAction.class;
	}

	@Override
	public void rollback(LogoutAction arg0, LogoutResult arg1,
			ExecutionContext arg2) throws DispatchException {
		// TODO Auto-generated method stub
		
	}

}
