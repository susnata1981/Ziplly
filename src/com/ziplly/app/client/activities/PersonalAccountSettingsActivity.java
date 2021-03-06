package com.ziplly.app.client.activities;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.InvalidCredentialsException;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdatePasswordResult;

public class PersonalAccountSettingsActivity extends AbstractAccountSettingsActivity<PersonalAccountDTO, PersonalAccountSettingsActivity.IPersonalAccountSettingsView>  
	implements AccountSettingsPresenter<PersonalAccountDTO>{

	public static interface IPersonalAccountSettingsView extends ISettingsView<PersonalAccountDTO, AccountSettingsPresenter<PersonalAccountDTO>> {
	}
	
	public PersonalAccountSettingsActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			IPersonalAccountSettingsView view) {
		super(dispatcher, eventBus, placeController, ctx, view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (ctx.getAccount() == null) {
			placeController.goTo(new LoginPlace());
		}
		
		// hack, hate hate hate
		if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountSettingsPlace());
		}
		
		bind();
		setImageUploadFormSubmitCompleteHandler();
		setUploadFormActionUrl();
		view.displaySettings((PersonalAccountDTO)ctx.getAccount());
		panel.setWidget(view);
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void cancel() {
		placeController.goTo(new PersonalAccountPlace());
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

	@Override
	public void onProfileLinkClick() {
		placeController.goTo(new PersonalAccountPlace());
	}

	@Override
	public void onInboxLinkClick() {
		placeController.goTo(new ConversationPlace());
	}
}
