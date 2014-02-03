package com.ziplly.app.client.activities;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.ResidentsView;
import com.ziplly.app.client.view.ResidentsView.EntityListViewPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class ResidentActivity extends AbstractActivity implements EntityListViewPresenter, SendMessagePresenter {
	private ResidentsView view;
	private ResidentPlace place;
	private EntityListHandler handler = new EntityListHandler();
	private AcceptsOneWidget panel;

	@Inject
	public ResidentActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx, ResidentPlace place,
			ResidentsView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		this.place = place;
		setupHandlers();
	}

	protected void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				displayInitalRange();
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		if (ctx.getAccount() != null) {
			displayInitalRange();
		} else {
			checkLoginStatus();
			this.panel = panel;
		}
	}

	protected void displayInitalRange() {
		if (place.getAccountId() != null) {
			view.displaySendMessageWidget(place.getAccountId());
			updateMessageWidgetWithAccountDetails(place.getAccountId());
		}

		GetEntityListAction action = new GetEntityListAction(EntityType.PERSONAL_ACCOUNT);
		action.setPage(0);
		action.setPageSize(view.getPageSize());
		action.setNeedTotalEntityCount(true);
		dispatcher.execute(action, handler);
	}

	private void updateMessageWidgetWithAccountDetails(Long accountId) {
		dispatcher.execute(new GetAccountByIdAction(accountId), new DispatcherCallbackAsync<GetAccountByIdResult>() {

			@Override
			public void onSuccess(GetAccountByIdResult result) {
				view.updateMessageWidget(result.getAccount());
			}
			
		});
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void getPersonalAccountList(GetEntityListAction action) {
		dispatcher.execute(action, handler);
	};

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	private class EntityListHandler extends DispatcherCallbackAsync<GetEntityResult> {
		@Override
		public void onSuccess(GetEntityResult result) {
			List<PersonalAccountDTO> accounts = new ArrayList<PersonalAccountDTO>();
			if (result.getEntityType() == EntityType.PERSONAL_ACCOUNT) {
				for (AccountDTO acct : result.getAccounts()) {
					accounts.add((PersonalAccountDTO) acct);
				}
			}

			if (result.getCount() != null) {
				view.setTotalRowCount(result.getCount());
			}
			view.display(accounts);
			panel.setWidget(view);
		}

		public void onFailure(Throwable caught) {
			// TODO
			Window.alert(caught.getLocalizedMessage());
		}
	}

	@Override
	public void sendMessage(ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}
		
		// make sure user is logged in
		if (ctx.getAccount() == null) {
			goTo(new LoginPlace());
			return;
		}
		
		// TODO check size
		int size = conversation.getMessages().size();
		conversation.getMessages().get(size-1).setSender(ctx.getAccount());
		conversation.setSender(ctx.getAccount());
		dispatcher.execute(new SendMessageAction(conversation), new DispatcherCallbackAsync<SendMessageResult>() {
			@Override
			public void onSuccess(SendMessageResult result) {
				view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
			}
		});
	}

}
