package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.BusinessAccountSettingsView.BusinessAccountSettingsPresenter;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanResult;
import com.ziplly.app.shared.GetJwtTokenAction;
import com.ziplly.app.shared.GetJwtTokenResult;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.PayResult;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class BusinessAccountSettingsActivity extends AbstractAccountSettingsActivity<BusinessAccountDTO, BusinessAccountSettingsActivity.IBusinessAccountSettingView>
	implements BusinessAccountSettingsPresenter {

	public static interface IBusinessAccountSettingView extends ISettingsView<BusinessAccountDTO, BusinessAccountSettingsPresenter>{
		void displayTransactionHistory(TransactionDTO transactionDTO);
		void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans);
		void setJwtString(String jwt);
		void displayPaymentStatus(String msg, AlertType type);
		void clearPaymentStatus();
		void disableSubscriptionButton();
		void hideTransactionHistory();
	}
	
	private String jwt;
	
	@Inject
	public BusinessAccountSettingsActivity(
			CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			ApplicationContext ctx, 
			IBusinessAccountSettingView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (ctx.getAccount() == null) {
			placeController.goTo(new LoginPlace());
			return;
		}
		bind();
		view.displaySettings((BusinessAccountDTO)ctx.getAccount());
		BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
		if (account.getTransaction() == null) {
			System.out.println("No transaction hisotry...");
			displaySubscriptionPlans();
		} else {
			System.out.println("Displaying transaction history..."+account.getTransaction().getTransactionId()+" name:"+account.getTransaction().getPlan().getName());
			displayTransactionHistory(account.getTransaction());
		}
		getSubscriptionPlans();
		setImageUploadFormSubmitCompleteHandler();
		setUploadFormActionUrl();
		panel.setWidget(view);
	}

	private void displayTransactionHistory(TransactionDTO txn) {
		view.displayTransactionHistory(txn);
		view.disableSubscriptionButton();
	}

	private void displaySubscriptionPlans() {
		getJwtString();
		view.hideTransactionHistory();
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void fetchData() {
		getSubscriptionPlans();
		getJwtString();
	}

	private void getSubscriptionPlans() {
		dispatcher.execute(new GetAllSubscriptionPlanAction(), new DispatcherCallbackAsync<GetAllSubscriptionPlanResult>() {
			@Override
			public void onSuccess(GetAllSubscriptionPlanResult result) {
				if (result != null) {
					System.out.println("Plans = "+result.getPlans().get(0).getName());
					view.displaySubscriptionPlans(result.getPlans());
				}
			}
		});
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void cancel() {
		placeController.goTo(new BusinessAccountPlace());
	}

	@Override
	public void getJwtString() {
		dispatcher.execute(new GetJwtTokenAction(), new DispatcherCallbackAsync<GetJwtTokenResult>() {

			@Override
			public void onSuccess(GetJwtTokenResult result) {
				BusinessAccountSettingsActivity.this.jwt = result.getToken();
				view.setJwtString(jwt);
			}
		});
	}

	@Override
	public void pay(TransactionDTO txn) {
		if (txn == null) {
			throw new IllegalArgumentException();
		}
		
		dispatcher.execute(new PayAction(txn), new DispatcherCallbackAsync<PayResult>() {
			@Override
			public void onSuccess(PayResult result) {
				view.displayPaymentStatus("successfully made payment", AlertType.SUCCESS);
				displayTransactionHistory(result.getTransaction());
			}
			
			public void onFailure(Throwable th) {
				view.displayPaymentStatus("Failed to make payment", AlertType.ERROR);
			}
		});
	}

	@Override
	public void updatePassword(UpdatePasswordAction action) {
		updatePassword(action, new DispatcherCallbackAsync<UpdatePasswordResult>() {
			@Override
			public void onSuccess(UpdatePasswordResult result) {
				view.displayMessage(StringConstants.PASSWORD_UPDATED, AlertType.SUCCESS);
			}
			
			public void onFailure(Throwable th) {
				if (th instanceof InvalidCredentialsException) {
					view.displayMessage(th.getMessage(), AlertType.ERROR);
				} else {
					view.displayMessage(StringConstants.PASSWORD_UPDATE_FAILURE, AlertType.ERROR);
				}
			}
		});		
	}

}
