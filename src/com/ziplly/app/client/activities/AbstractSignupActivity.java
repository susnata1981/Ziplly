package com.ziplly.app.client.activities;

import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.widget.LoginWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.CheckEmailRegistrationAction;
import com.ziplly.app.shared.CheckEmailRegistrationResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.RegisterAccountResult;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.ValidateLoginResult;

public abstract class AbstractSignupActivity extends AbstractActivity implements SignupActivityPresenter {
	ISignupView<SignupActivityPresenter> view;
	Logger logger = Logger.getLogger("AbstractSignupActivity");
	ValidateLoginHandler validateLoginHandler = new ValidateLoginHandler();
	RegisterAccountHandler registerAccountHandler = new RegisterAccountHandler();
	
	public AbstractSignupActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			ISignupView<SignupActivityPresenter> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}

	@Override
	public void onLogin(String email, String password) {
		dispatcher.execute(new ValidateLoginAction(email, password), 
				validateLoginHandler);
	}

	@Override
	public void register(AccountDTO account) {
		dispatcher.execute(new RegisterAccountAction(account),
				registerAccountHandler);
	}

	@Override
	public void setImageUploadUrl() {
		dispatcher.execute(new GetImageUploadUrlAction(),
				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
					@Override
					public void onSuccess(GetImageUploadUrlResult result) {
						// TODO hack for making it work in local environment
						String url = result.getImageUrl().replace(
								"susnatas-MacBook-Pro.local:8888",
								"127.0.0.1:8888");
						System.out.println("Setting upload image form action to:"+url);
						view.setImageUploadUrl(url);
					}
				});
	}

	@Override
	public void getNeighborhoodData(String postalCode) {
		dispatcher.execute(new GetNeighborhoodAction(postalCode), 
				new NeighborhoodHandler());
	}
	
	// TODO handle image deletion on multiple file uploads
	@Override
	public void setUploadImageHandler() {
		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:"+imageUrl);
				view.displayProfileImagePreview(imageUrl);
				view.reset();
				setImageUploadUrl();
			}
		});
	}

	@Override
	public void addToInviteList(String email, String postalCode) {
		// need to implement.
	}
	
	public void verifyInvitationForEmail(final AccountDTO account, long code) {
		CheckEmailRegistrationAction action = new CheckEmailRegistrationAction(account.getEmail(), code);
		dispatcher.execute(action, new CheckEmailRegistrationHandler(account));
	}
	
	@Override
	public void register(AccountDTO account, String code) {
		
		if (code == null) {
			view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
			return;
		}
		
		if (account != null && code != null ) {
			try {
				long c = Long.parseLong(code);
				verifyInvitationForEmail(account, c);
			} catch(NumberFormatException nf) {
				view.displayMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				return;
			}
		} else {
			view.displayMessage(StringConstants.NEEDS_INVITATION, AlertType.ERROR);
		}
	}

	public class ValidateLoginHandler extends DispatcherCallbackAsync<ValidateLoginResult> {
	
		@Override
		public void onSuccess(ValidateLoginResult result) {
			if (result != null && result.getAccount() != null) {
				ctx.setAccount(result.getAccount());
				forward(result.getAccount());
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			if (caught instanceof NotFoundException) {
				view.displayMessage(
						LoginWidget.ACCOUNT_DOES_NOT_EXIST,
						AlertType.ERROR);
			} else if (caught instanceof InvalidCredentialsException) {
				view.displayMessage(
						LoginWidget.INVALID_ACCOUNT_CREDENTIALS,
						AlertType.ERROR);
			}
			view.resetLoginForm();
		}
	}
	
	public class RegisterAccountHandler extends DispatcherCallbackAsync<RegisterAccountResult> {
		
		@Override
		public void onSuccess(RegisterAccountResult result) {
			System.out.println("Account " + result.getAccount()
					+ " registered.");
			eventBus.fireEvent(new LoginEvent(result.getAccount()));
			view.clear();
			forward(result.getAccount());
		}
		
		public void onFailure(Throwable th) {
			if (th instanceof AccountExistsException) {
				view.displayMessage(StringConstants.EMAIL_ALREADY_EXISTS, AlertType.ERROR);
				return;
			}
			view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
		}
	}
	
	public class CheckEmailRegistrationHandler extends DispatcherCallbackAsync<CheckEmailRegistrationResult> {
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
		}
	}
}
