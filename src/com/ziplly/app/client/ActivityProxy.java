package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public final class ActivityProxy<T extends Activity> implements Activity {
	private final AsyncProvider<T> activityProvider;
	private Activity activity;
	private boolean isCancelled;

	@Inject
	public ActivityProxy(AsyncProvider<T> activityProvider) {
		this.activityProvider = activityProvider;
	}

	@Override
	public String mayStop() {
		if (activity != null) {
			return activity.mayStop();
		}
		return null;
	}

	@Override
	public void onCancel() {
		isCancelled = true;
		if (activity != null) {
			activity.onCancel();
		}
	}

	@Override
	public void onStop() {
		if (activity != null) {
			activity.onStop();
		}
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// TODO:
			}

			@Override
			public void onSuccess() {
				activityProvider.get(new AsyncCallback<T>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(T result) {
						activity = result;
						if (!isCancelled) {
							activity.start(panel, eventBus);
						}
					}
				});
			}

		});
	}
}