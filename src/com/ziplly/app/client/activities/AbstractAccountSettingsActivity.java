package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public abstract class AbstractAccountSettingsActivity<T extends AccountDTO, V extends ISettingsView<T, ? extends AccountSettingsPresenter<T>>>
    extends AbstractActivity {
	protected V view;
	
	public AbstractAccountSettingsActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
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
		eventBus.fireEvent(new LoadingEventStart());
		dispatcher.execute(
		    new UpdateAccountAction(account),
		    new DispatcherCallbackAsync<UpdateAccountResult>(eventBus) {
			    @Override
			    public void onSuccess(UpdateAccountResult result) {
				    // Fire event.
			    	view.displayMessage(stringDefinitions.accountUpdated(), AlertType.SUCCESS);
				    // Update account and fire event.
				    ctx.setAccount(result.getAccount());
				    eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));

				    view.showSaveButton(true);
				    eventBus.fireEvent(new LoadingEventEnd());
			    }

			    @Override
			    public void onFailure(Throwable th) {
				    view.displayMessage(StringConstants.FAILED_TO_SAVE_ACCOUNT, AlertType.ERROR);
				    view.showSaveButton(true);
				    eventBus.fireEvent(new LoadingEventEnd());
			    }
		    });
	}

	public void updatePassword(UpdatePasswordAction action,
	    DispatcherCallbackAsync<UpdatePasswordResult> callback) {
		dispatcher.execute(action, callback);
	}

	public void setUploadFormActionUrl() {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    // TODO hack for making it work in local environment
				    String url =
				        result.getImageUrl().replace("susnatas-MacBook-Pro.local:8888", "127.0.0.1:8888");
				    System.out.println("Setting upload image form action to:" + url);
				    view.setUploadFormActionUrl(url);
			    }
			    
			    @Override
			    public void onFailure(Throwable th) {
			    	Window.alert("Failed to set upload link");
			    }
		    });
	}

	// TODO handle image deletion on multiple file uploads
	public void setImageUploadFormSubmitCompleteHandler() {
		view.setUploadFormSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:" + imageUrl);
				if (imageUrl == null || "".equals(imageUrl)) {
					view.displayMessage(StringConstants.INVALID_IMAGE, AlertType.ERROR);
				} else {
					view.displayImagePreview(imageUrl);
				}
				resetUploadForm();
				eventBus.fireEvent(new LoadingEventEnd());
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
