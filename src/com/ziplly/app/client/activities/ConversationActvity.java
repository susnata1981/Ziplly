package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.ConversationView;
import com.ziplly.app.client.view.IConversationView;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.ViewConversationAction;
import com.ziplly.app.shared.ViewConversationResult;

public class ConversationActvity extends AbstractActivity implements ConversationView.ConversationViewPresenter {
	private IConversationView view;
	private AcceptsOneWidget panel;

	@Inject
	public ConversationActvity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, PlaceController placeController,
			ApplicationContext ctx,
			ConversationView view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (ctx.getAccount() == null) {
			placeController.goTo(new LoginPlace());
			return;
		}
		
		this.panel = panel;
		bind();
		getConversations();
		setupHandlers();
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
	}

	@Override
	public void getConversations() {
		dispatcher.execute(new GetConversationsAction(), new DispatcherCallbackAsync<GetConversationsResult>() {
			@Override
			public void onSuccess(GetConversationsResult result) {
				view.displayConversations(result.getConversations());
				go(panel);
			}
		});
	}

	@Override
	public void fetchData() {
	}

	@Override
	public void go(AcceptsOneWidget container) {
		panel.setWidget(view);
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
		System.out.println("sending message to: "+conversation.getReceiver());
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
					eventBus.fireEvent(new AccountDetailsUpdateEvent());
				}
			});
		}
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
}
