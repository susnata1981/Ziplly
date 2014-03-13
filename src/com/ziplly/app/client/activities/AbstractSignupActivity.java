package com.ziplly.app.client.activities;

import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.AddInvitationAction;
import com.ziplly.app.shared.AddInvitationResult;
import com.ziplly.app.shared.CheckEmailRegistrationAction;
import com.ziplly.app.shared.CheckEmailRegistrationResult;
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteImageResult;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;
import com.ziplly.app.shared.NeighborhoodSearchActionType;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;

public abstract class AbstractSignupActivity extends AbstractActivity implements
    SignupActivityPresenter {
	ISignupView<SignupActivityPresenter> view;
	Logger logger = Logger.getLogger(AbstractSignupActivity.class.getName());
	RegisterAccountHandler registerAccountHandler = new RegisterAccountHandler();

	public AbstractSignupActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    ISignupView<SignupActivityPresenter> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		setupHandlers();
	}

	@Override
	public void register(AccountDTO account) {
		eventBus.fireEvent(new LoadingEventStart());
		dispatcher.execute(new RegisterAccountAction(account), registerAccountHandler);
	}

	@Override
	public void getNeighborhoodData(String postalCode) {
		view.displayNeighborhoodListLoading(true);
		GetNeighborhoodAction action = new GetNeighborhoodAction(postalCode);
		action.setSearchType(NeighborhoodSearchActionType.BY_ZIP);
		dispatcher.execute(action, new NeighborhoodHandler());
	}

	@Override
	public void addToInviteList(String email, int postalCode) {
		AddInvitationAction action = new AddInvitationAction(email, postalCode);
		dispatcher.execute(action, new DispatcherCallbackAsync<AddInvitationResult>() {

			@Override
			public void onSuccess(AddInvitationResult result) {
				view.displayMessage(StringConstants.EMAIL_SAVED_FOR_INVITATION, AlertType.SUCCESS);
			}
		});
	}

	public void verifyInvitationForEmail(final AccountDTO account, long code) {
		CheckEmailRegistrationAction action =
		    new CheckEmailRegistrationAction(account.getEmail(), code);
		dispatcher.execute(action, new CheckEmailRegistrationHandler(account));
	}

	@Override
	public void register(AccountDTO account, String code) {

		if (code == null) {
			view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
			return;
		}

		if (account != null && code != null) {
			try {
				long c = Long.parseLong(code);
				verifyInvitationForEmail(account, c);
			} catch (NumberFormatException nf) {
				view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				return;
			}
		} else {
			view.displayMessage(StringConstants.NEEDS_INVITATION, AlertType.ERROR);
		}
	}

	/**
	 * Deletes the image from blobstore based on serving url
	 */
	@Override
	public void deleteImage(String url) {
		dispatcher.execute(
		    new DeleteImageAction(url),
		    new DispatcherCallbackAsync<DeleteImageResult>() {
			    @Override
			    public void onSuccess(DeleteImageResult result) {
				    // Nothing to do.
			    }
		    });
	}

	public class RegisterAccountHandler extends DispatcherCallbackAsync<RegisterAccountResult> {

		@Override
		public void onSuccess(RegisterAccountResult result) {
			// String property =
			// System.getProperty(StringConstants.EMAIL_VERIFICATION_FEATURE_FLAG,
			// "true");
			// boolean isEmailVerificationRequired = Boolean.valueOf(property);

			if (result.getAccount().getStatus() != AccountStatus.ACTIVE) {
				view.clear();
				view.displayMessage(StringConstants.EMAIL_VERIFICATION_SENT, AlertType.SUCCESS);
			} else {
				ctx.setAccount(result.getAccount());
				eventBus.fireEvent(new LoginEvent(result.getAccount()));
				view.clear();
				forward(result.getAccount());
			}
			eventBus.fireEvent(new LoadingEventEnd());
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof AccountExistsException) {
				view.displayMessage(StringConstants.EMAIL_ALREADY_EXISTS, AlertType.ERROR);
			} else {
				view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
			eventBus.fireEvent(new LoadingEventEnd());
		}
	}

	public class CheckEmailRegistrationHandler extends
	    DispatcherCallbackAsync<CheckEmailRegistrationResult> {
		private AccountDTO account;

		public CheckEmailRegistrationHandler(AccountDTO account) {
			this.account = account;
		}

		@Override
		public void onSuccess(CheckEmailRegistrationResult result) {
			if (account instanceof BusinessAccountDTO) {
				((BusinessAccountDTO) account).setBusinessType(result.getBusinessType());
			}
			register(account);
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof AccessError) {
				view.displayMessage(StringConstants.NEEDS_INVITATION, AlertType.ERROR);
			}
		}
	}

	public class NeighborhoodHandler extends DispatcherCallbackAsync<GetNeighborhoodResult> {
		@Override
		public void onSuccess(GetNeighborhoodResult result) {
			if (result.getNeighbordhoods() != null && result.getNeighbordhoods().size() > 0) {
				view.displayNeighborhoods(result.getNeighbordhoods());
			} else {
				view.displayNotYetLaunchedWidget();
				view.displayMessage(StringConstants.NOT_AVAILABLE_IN_AREA, AlertType.ERROR);
			}
			view.displayNeighborhoodListLoading(false);
		}
	}
}
