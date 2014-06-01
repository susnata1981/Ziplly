package com.ziplly.app.server.bli;

import java.util.Date;

import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.server.model.jpa.Session;

public class SessionBLIImpl implements SessionBLI {

	private Provider<HttpSession> httpSessionProvider;
	private SessionDAO sessionDao;

	@Inject
	public SessionBLIImpl(SessionDAO sessionDao, Provider<HttpSession> httpSessionProvider) {
		this.sessionDao = sessionDao;
		this.httpSessionProvider = httpSessionProvider;
  }
	
	@Override
  public Session validateSession() throws NeedsLoginException {
		Long existingUid = (Long) httpSessionProvider.get().getAttribute(ZipllyServerConstants.SESSION_ID);
		if (existingUid != null) {
			try {
				Session session = sessionDao.findSessionByUid(existingUid);
				if (isValidSession(session)) {
					throw new NeedsLoginException();
				}
				
				return session;
			} catch (NotFoundException e) {
				
			}
		}
		throw new NeedsLoginException();
  }

	private boolean isValidSession(Session session) {
		if (session == null) {
			return false;
		}
		return session.getExpireAt().after(new Date());
	}
}
