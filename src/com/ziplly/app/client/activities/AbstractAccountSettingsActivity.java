package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public abstract class AbstractAccountSettingsActivity<T extends AccountDTO, 
	V extends ISettingsView<T,? extends AccountSettingsPresenter<T>>> extends AbstractActivity {
	protected V view;

	public AbstractAccountSettingsActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			V view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}
	
	public void save(T account) {
		// provide implementation;
		if (account == null) {
			throw new IllegalArgumentException();
		}
		
		dispatcher.execute(new UpdateAccountAction(account), new DispatcherCallbackAsync<UpdateAccountResult>() {
			@Override
			public void onSuccess(UpdateAccountResult result) {
				// fire event;
				view.displayMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
				eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));
			}
		});
	}
	
	public void updatePassword(UpdatePasswordAction action, DispatcherCallbackAsync<UpdatePasswordResult> callback) {
		dispatcher.execute(action, callback);
	}
	
	// TODO centralize url transformation code
	public void setUploadFormActionUrl() {
		dispatcher.execute(new GetImageUploadUrlAction(),
				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
					@Override
					public void onSuccess(GetImageUploadUrlResult result) {
						// TODO hack for making it work in local environment
						String url = result.getImageUrl().replace(
								"susnatas-MacBook-Pro.local:8888",
								"127.0.0.1:8888");
						System.out.println("Setting upload image form action to:"+url);
						view.setUploadFormActionUrl(url);
					}
				});
	}

	// TODO handle image deletion on multiple file uploads
	public void setImageUploadFormSubmitCompleteHandler() {
		view.setUploadFormSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:"+imageUrl);
				view.displayImagePreview(imageUrl);
				resetUploadForm();
			}
		});
	}
	
	void resetUploadForm() {
		view.resetUploadForm();
		setUploadFormActionUrl();
	}
	
	@Override
	public void onStop() {
		view.clear();
	}
}
