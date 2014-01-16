package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.ConversationView;
import com.ziplly.app.client.view.IConversationView;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.ViewConversationAction;
import com.ziplly.app.shared.ViewConversationResult;

public class ConversationActvity extends AbstractActivity implements ConversationView.ConversationViewPresenter {
	private IConversationView view;
	private AcceptsOneWidget panel;
	private GetConversationHandler handler = new GetConversationHandler();
	private SingleConversationHandler singleConversationHandler = new SingleConversationHandler();
	private ConversationPlace place;
	
	@Inject
	public ConversationActvity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			ApplicationContext ctx,
			ConversationPlace place, 
			ConversationView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.view = view;
		setupHandlers();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		bind();
		go(panel);
		if (ctx.getAccount() != null) {
			internalStart();
		}
		checkLoginStatus();
	}

	private void setupHandlers() {
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {
			@Override
			public void onEvent(AccountDetailsUpdateEvent event) {
				dispatcher.execute(new GetConversationsAction(), new DispatcherCallbackAsync<GetConversationsResult>() {
					@Override
					public void onSuccess(GetConversationsResult result) {
						view.setMessageCount(result.getConversations());
					}
				});
			}
		});
		
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				internalStart();
			}
		});
	}
	
	private void internalStart() {
		if (place.getConversationId() != null) {
			GetConversationsAction action = new GetConversationsAction();
			action.setType(ConversationType.SINGLE);
			action.setConversationId(place.getConversationId());
			dispatcher.execute(action, singleConversationHandler); 
		} else {
			GetConversationsAction action = new GetConversationsAction();
			action.setType(ConversationType.ALL);
			action.setStart(0);
			action.setPageSize(0);
			action.setGetTotalConversation(true);
			dispatcher.execute(action, handler); 
		}
	}

	@Override
	public void getConversations(ConversationType type, int start, int pageSize) {
		GetConversationsAction action = new GetConversationsAction();
		action.setType(type);
		action.setStart(start);
		action.setPageSize(pageSize);
		dispatcher.execute(action, handler); 
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		view.clear();
	}
	
	@Override
	public void bind() {
		view.setPresenter(this);
	}

	/*
	 * Send message
	 * @see com.ziplly.app.client.activities.AccountPresenter#sendMessage(com.ziplly.app.model.MessageDTO)
	 */
	@Override
	public void sendMessage(final ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}
		
		dispatcher.execute(new SendMessageAction(conversation), new DispatcherCallbackAsync<SendMessageResult>() {
			@Override
			public void onSuccess(SendMessageResult result) {
				view.updateConversation(conversation);
			}
		});
	}

	@Override
	public void onView(ConversationDTO conversation) {
		if (conversation != null) {
			// update the status column only if it's the receiver
			if (conversation.isSender()) {
				return;
			}
			
			dispatcher.execute(new ViewConversationAction(conversation.getId()), new DispatcherCallbackAsync<ViewConversationResult>() {
				@Override
				public void onSuccess(ViewConversationResult result) {
					updateAccountDetails();
				}
			});
		}
	}

	private void updateAccountDetails() {
		dispatcher.execute(new GetAccountDetailsAction(), new DispatcherCallbackAsync<GetAccountDetailsResult>() {
			@Override
			public void onSuccess(GetAccountDetailsResult result) {
				eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
			}
		});
	}
	
	@Override
	public void gotoProfile() {
		if (ctx.getAccount() instanceof PersonalAccountDTO) {
			placeController.goTo(new PersonalAccountPlace());
			return;
		}
		else if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountPlace());
			return;
		}
		throw new IllegalArgumentException("Invalid account type");
	}

	private class GetConversationHandler extends DispatcherCallbackAsync<GetConversationsResult> {
		@Override
		public void onSuccess(GetConversationsResult result) {
			if (result.getTotalConversations() != null) {
				view.setTotalConversation(result.getTotalConversations());
			}
			view.displayConversations(result.getConversations());
		}
	};
	
	private class SingleConversationHandler extends DispatcherCallbackAsync<GetConversationsResult> {
		@Override
		public void onSuccess(GetConversationsResult result) {
			if (result.getConversations().size() == 1) {
				view.displayConversation(result.getConversations().get(0));
			}
		}
		
		@Override
		public void onFailure(Throwable th) {
			Window.alert(th.getMessage());
		}
	};
}
