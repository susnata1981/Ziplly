package com.ziplly.app.server.handlers;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import org.jboss.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.server.ZipllyServerConstants;
import com.ziplly.app.server.bli.SessionBLI;
import com.ziplly.app.server.model.jpa.Session;

public abstract class AbstractSessionAwareActionHandler<T extends Action<R>, R extends Result> implements
    ActionHandler<T, R> {
  @SuppressWarnings("unused")
  private Provider<EntityManager> entityManagerProvider;
  private Logger logger = Logger.getLogger(AbstractSessionAwareActionHandler.class);

  @Inject
  protected Provider<HttpSession> httpSession;
  
  @Inject
  private SessionBLI sessionBli;
  
  public AbstractSessionAwareActionHandler(Provider<EntityManager> entityManagerProvider) {
    this.entityManagerProvider = entityManagerProvider;
  }

  @Override
  public R execute(T action, ExecutionContext context) throws DispatchException {
    try {
      preHandler();
      R result = doExecute(action, context);
      postHandler(result);
      return result;
    } catch (NeedsLoginException e) {
      throw new NeedsLoginException("Please login first");
    } catch (Exception ex) {
      logger.debug(String.format("FAILED TO PROCESS REQUEST %s", ex));
      throw ex;
    }
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
  
  protected void postHandler(R result) throws InternalException {
  }

  protected void preHandler() throws DispatchException {
  }

  @Override
  public abstract Class<T> getActionType();

  @Override
  public void rollback(T arg0, R arg1, ExecutionContext arg2) throws DispatchException {
  }

  public abstract R doExecute(T action, ExecutionContext context) throws DispatchException;
}
