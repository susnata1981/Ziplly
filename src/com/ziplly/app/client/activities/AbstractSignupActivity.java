package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.client.exceptions.AccountExistsException;
import com.ziplly.app.client.view.ISignupView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.signup.SignupViewMessages;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.NeighborhoodDTO;
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
  
  private SignupViewMessages signupViewMessages = GWT.create(SignupViewMessages.class);
	protected ISignupView<SignupActivityPresenter> view;

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
		dispatcher.execute(new RegisterAccountAction(account), new RegisterAccountHandler(eventBus, account));
	}

	@Deprecated
	@Override
	public void getNeighborhoodData(String postalCode) {
		view.displayNeighborhoodListLoading(true);
		GetNeighborhoodAction action = new GetNeighborhoodAction(postalCode);
		action.setSearchType(NeighborhoodSearchActionType.BY_ZIP);
		dispatcher.execute(action, new NeighborhoodHandler());
	}

	@Override
  public void getNeighborhoodData(NeighborhoodDTO n) {
		view.displayNeighborhoodListLoading(true); 
		GetNeighborhoodAction action = new GetNeighborhoodAction();
		action.setNeighborhood(n);
		action.setSearchType(NeighborhoodSearchActionType.BY_NEIGHBORHOOD);
		dispatcher.execute(action, new NeighborhoodHandler());
  }
	
	@Override
	public void createNeighborhood(NeighborhoodDTO n) {
		GetNeighborhoodAction action = new GetNeighborhoodAction();
		action.setNeighborhood(n);
		action.setSearchType(NeighborhoodSearchActionType.BY_NEIGHBORHOOD);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetNeighborhoodResult>(eventBus) {

			@Override
      public void onSuccess(GetNeighborhoodResult result) {
				view.displayNewNeighborhood(result.getNeighbordhoods().get(0));
      }
			
			@Override
			public void onFailure(Throwable th) {
				view.displayErrorDuringNeighborhoodSelection(StringConstants.FAILED_TO_ADD_NEIGHBORHOOD, AlertType.ERROR);
			}
		});
	}
	
	public void setCurrentNeighborhood(NeighborhoodDTO currNeighborhood) {
		view.displayNeighborhood(currNeighborhood);
	}
	
	public void getNeighborhoodList(NeighborhoodDTO rootNeighborhood) {
		GetNeighborhoodAction action = new GetNeighborhoodAction();
		action.setNeighborhood(rootNeighborhood);
		action.setSearchType(NeighborhoodSearchActionType.BY_NEIGHBORHOOD_LOCALITY);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetNeighborhoodResult>() {

			@Override
      public void onSuccess(GetNeighborhoodResult result) {
				view.displayNeighborhoodList(result.getNeighbordhoods());
      }
			
			@Override
			public void onFailure(Throwable th) {
				view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
			}
		});
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
	  private AccountDTO account;

    public RegisterAccountHandler(EventBus eventBus, AccountDTO account) {
      super(eventBus);
	    this.account = account;
    }
    
		@Override
		public void onSuccess(RegisterAccountResult result) {
			if (result.getAccount().getStatus() != AccountStatus.ACTIVE) {
				view.clear();
				view.displayMessage(StringConstants.EMAIL_VERIFICATION_SENT, AlertType.SUCCESS);
			} 
			eventBus.fireEvent(new LoadingEventEnd());
		}

		@Override
		public void onFailure(Throwable th) {
			if (th instanceof AccountExistsException) {
				view.displayMessage(signupViewMessages.duplicateAccount(account.getEmail()), AlertType.ERROR);
			} else {
				view.displayMessage(stringDefinitions.internalError(), AlertType.ERROR);
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
			if (th instanceof AccessException) {
				view.displayMessage(StringConstants.NEEDS_INVITATION, AlertType.ERROR);
			}
		}
	}

	public class NeighborhoodHandler extends DispatcherCallbackAsync<GetNeighborhoodResult> {
		@Override
		public void onSuccess(GetNeighborhoodResult result) {
			view.displayNeighborhoodListLoading(false);
			view.displayNeighborhood(result.getNeighbordhoods().get(0));
		}
	}
}
