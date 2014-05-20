package com.ziplly.app.client.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountNotificationEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoadingEventEndHandler;
import com.ziplly.app.client.view.handler.LoadingEventStartHandler;
import com.ziplly.app.client.widget.LoadingPanelWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountNotificationResult;
import com.ziplly.app.shared.GetEnvironmentAction;
import com.ziplly.app.shared.GetEnvironmentResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetLoggedInUserResult;

public abstract class AbstractActivity implements Activity {
	protected CachingDispatcherAsync dispatcher;
	protected PlaceController placeController;
	protected EventBus eventBus;
	protected ApplicationContext ctx;
	protected LoadingPanelWidget loadingModal;
	// Maximum number of target neighborhoods visible
	private int maxNeighborhoodsVisible = 2;
	protected StringDefinitions stringDefinitions = GWT.create(StringDefinitions.class);
	
	public AbstractActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
		loadingModal = new LoadingPanelWidget();
		initializeEnvironment();
	}

	private void initializeEnvironment() {
		if (!ctx.isEnvironmentSet()) {
			dispatcher.execute(
			    new GetEnvironmentAction(),
			    new DispatcherCallbackAsync<GetEnvironmentResult>() {

				    @Override
				    public void onSuccess(GetEnvironmentResult result) {
					    ctx.setEnvironment(result.getEnvironment());
				    }
			    });
		}
	}

	public Environment getEnvironment() {
		return ctx.getEnvironment();
	}

	protected void setupHandlers() {
		eventBus.addHandler(LoadingEventStart.TYPE, new LoadingEventStartHandler() {

			@Override
			public void onEvent(LoadingEventStart event) {
				showLodingIcon();
			}
		});

		eventBus.addHandler(LoadingEventEnd.TYPE, new LoadingEventEndHandler() {

			@Override
			public void onEvent(LoadingEventEnd event) {
				hideLoadingIcon();
			}
		});
	}

	void showLodingIcon() {
		loadingModal.show(true);
	}

	void hideLoadingIcon() {
		loadingModal.show(false);
	}

	public void goTo(Place place) {
		placeController.goTo(place);
	}

	@Override
	public String mayStop() {
		return null;
	}

	@Override
	public void onCancel() {
	}

	@Override
	public void onStop() {
	}

	/**
	 * Checks to see if user is logged in and calls doStart if user is logged in.
	 * Otherwise it forwards the user to LoginView.
	 */
	public void checkAccountLogin() {
		// If user already logged in then just call doStart.
		if (userLoggedIn()) {
			doStart();
			return;
		}

		GetLoggedInUserAction action = new GetLoggedInUserAction();
		dispatcher.execute(action, new DispatcherCallbackAsync<GetLoggedInUserResult>() {
			@Override
			public void onSuccess(GetLoggedInUserResult result) {
				if (result != null && result.getAccount() != null) {
					ctx.setAccount(result.getAccount());
					eventBus.fireEvent(new LoginEvent(result.getAccount()));
					doStart();
				} else {
					// This could be overridden by client.
					doStartOnUserNotLoggedIn();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

		});
	}

	protected boolean userLoggedIn() {
		return ctx.getAccount() != null;
  }

	/**
	 * Activity needs to define behavior in case user is logged in.
	 */
	protected abstract void doStart();

	/**
	 * If user is not logged in, default behavior is to redirect to Login view.
	 */
	protected void doStartOnUserNotLoggedIn() {
		placeController.goTo(new LoginPlace());
	}

	protected void forward(AccountDTO acct) {
		if (acct != null) {
			if (acct instanceof PersonalAccountDTO) {
				goTo(new PersonalAccountPlace());
			} else if (acct instanceof BusinessAccountDTO) {
				goTo(new BusinessAccountPlace());
			}
		}
	}

	/**
	 * Get the list of target neighborhoods in child to parent order.
	 */
	protected List<NeighborhoodDTO> getTargetNeighborhoodList() {
		if (ctx.getAccount() != null) {
			List<NeighborhoodDTO> neighborhoods = new ArrayList<NeighborhoodDTO>();
			NeighborhoodDTO neighborhood = ctx.getCurrentNeighborhood();
			neighborhoods.add(neighborhood);
			NeighborhoodDTO parentNeighborhood = neighborhood.getParentNeighborhood();
			int count = 0;
			while (parentNeighborhood != null && count < maxNeighborhoodsVisible) {
				neighborhoods.add(parentNeighborhood);
				parentNeighborhood = parentNeighborhood.getParentNeighborhood();
				count++;
			}

			return neighborhoods;
		} else {
			return Collections.emptyList();
		}
	}

	public class AccountNotificationHandler extends
	    DispatcherCallbackAsync<GetAccountNotificationResult> {

		@Override
		public void onSuccess(GetAccountNotificationResult result) {
			eventBus.fireEvent(new AccountNotificationEvent(result.getAccountNotifications()));
		}
	}

	public static native void log(String msg) /*-{
		$wnd.console.log(msg);
	}-*/;

  public void initializeUploadForm(final FormUploadWidget formUploadWidget) {
    setFormUploadActionUrl(formUploadWidget);
    setupFormCompleteHandler(formUploadWidget);
  }
  
  private void setFormUploadActionUrl(final FormUploadWidget formUploadWidget) {
    dispatcher.execute(
        new GetImageUploadUrlAction(),
        new DispatcherCallbackAsync<GetImageUploadUrlResult>() {

          @Override
          public void onSuccess(GetImageUploadUrlResult result) {
            if (result.getImageUrl() != null) {
              formUploadWidget.resetUploadForm();
              formUploadWidget.setUploadFormActionUrl(result.getImageUrl());
              formUploadWidget.enableUploadButton();
            }
          }
        });
  }
  
  private void setupFormCompleteHandler(final FormUploadWidget formUploadWidget) {
    formUploadWidget.setUploadFormSubmitCompleteHandler(new SubmitCompleteHandler() {

      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        try {
          if (event.getResults() != null) {
//            ImageDTO imageDto = ImageUtil.parseImageUrl(event.getResults());
            formUploadWidget.displayImagePreview(event.getResults());
          }
        } catch (Error error) {
          Window.alert("Error");
        }
        setFormUploadActionUrl(formUploadWidget);
      }

    });
  }
}
