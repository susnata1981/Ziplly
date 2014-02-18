package com.ziplly.app.client.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class CachingDispatcherAsync implements DispatchAsync {
	Logger logger = Logger.getLogger(CachingDispatcherAsync.class.getName());
	HashMap<Action<? extends Result>, Result> cache = new HashMap<Action<? extends Result>, Result>();

	HashMap<Action<? extends Result>, ArrayList<AsyncCallback<Result>>> pendingCallbacks = new HashMap<Action<? extends Result>, ArrayList<AsyncCallback<Result>>>();

	private DispatchAsync dispatcher;

	@Inject
	public CachingDispatcherAsync(DispatchAsync dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public <A extends Action<R>, R extends Result> void execute(A action,
			final AsyncCallback<R> callback) {

		if (action instanceof Cacheable) {
			executeCache(action, callback);
		} else {
			dispatcher.execute(action, new AsyncCallback<R>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(R result) {
					callback.onSuccess(result);
				}
			});
		}
	}

	<A extends Action<R>, R extends Result> void executeCache(final A action,
			final AsyncCallback<R> callback) {

		ArrayList<AsyncCallback<Result>> callbackList = pendingCallbacks
				.get(action);

		if (callbackList != null) {
			logger.log(Level.INFO, "Pending async operation...");
			callbackList.add((AsyncCallback<Result>) callback);
			return;
		}

		Result cachedResult = cache.get(action);

		if (cachedResult != null) {
			logger.log(Level.INFO, "Cache hit");
			callback.onSuccess((R) cachedResult);
			return;
		}
		
		pendingCallbacks.put(action, new ArrayList<AsyncCallback<Result>>());
		logger.log(Level.INFO, "Cache miss");
		dispatcher.execute(action, new AsyncCallback<R>() {

			@Override
			public void onFailure(Throwable caught) {
				ArrayList<AsyncCallback<Result>> callbacks = pendingCallbacks
						.get(action);
				if (callbacks != null) {
					for (AsyncCallback<Result> pendingCallback : callbacks) {
						pendingCallback.onFailure(caught);
					}
					pendingCallbacks.get(action).clear();
				}
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(R result) {
				ArrayList<AsyncCallback<Result>> callbacks = pendingCallbacks
						.get(action);
				if (callbacks != null) {
					for (AsyncCallback<Result> pendingCallback : callbacks) {
						logger.log(Level.INFO, "Acknowledging pending callbacks");
						pendingCallback.onSuccess(result);
					}
					pendingCallbacks.get(action).clear();
				}
				cache.put(action, result);
				callback.onSuccess(result);
			}
		});
	}

	public void clear() {
		cache.clear();
	}

	public <A extends Action<R>, R extends Result> void clear(A action) {
		cache.remove(action);
	}
}
