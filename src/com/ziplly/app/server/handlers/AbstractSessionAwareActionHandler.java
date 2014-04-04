package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;
import net.customware.gwt.dispatch.shared.Result;

import com.google.inject.Provider;

public abstract class AbstractSessionAwareActionHandler<T extends Action<R>, R extends Result> implements ActionHandler<T, R> {
	private Provider<EntityManager> entityManagerProvider;

	public AbstractSessionAwareActionHandler(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
  }

	@Override
  public R execute(T action, ExecutionContext context) throws DispatchException {
		preHandler();
		R result = doExecute(action, context);
		postHandler();
		return result;
  }

	protected void postHandler() {
  }

	protected void preHandler() {
  }

	@Override
  public abstract Class<T> getActionType();

	@Override
  public void rollback(T arg0, R arg1, ExecutionContext arg2) throws DispatchException {
  }

	public abstract R doExecute(T action, ExecutionContext context) throws DispatchException;
}
