package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;

public abstract class AbstractAccountActivity<T extends AccountDTO> extends AbstractActivity implements AccountPresenter<T> {
	protected IAccountView<T> view;

	public AbstractAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			ApplicationContext ctx,
			IAccountView<T> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		
		eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {
			@Override
			public void onEvent(AccountUpdateEvent event) {
				AbstractAccountActivity.this.ctx.setAccount(event.getAccount());
				displayProfile();
			}
		});
	}

	@Override
	public void fetchData() {
	}
	
	@Override
	public void save(T account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						view.displayAccountUpdateSuccessfullMessage();
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
						view.displayAccountUpdateFailedMessage();
					}
				});
	}
	
	@Override
	public void tweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		
		dispatcher.execute(new TweetAction(tweet),
				new DispatcherCallbackAsync<TweetResult>() {
					@Override
					public void onSuccess(TweetResult result) {
						placeController.goTo(new HomePlace());
						view.clearTweet();
					}
				});
	}

	@Override
	public void logout() {
		dispatcher.execute(new LogoutAction(ctx.getAccount().getUid()),
				new DispatcherCallbackAsync<LogoutResult>() {
					@Override
					public void onSuccess(LogoutResult result) {
						eventBus.fireEvent(new LogoutEvent());
						ctx.setAccount(null);
						goTo(new LoginPlace());
					}
				});
	}

	@Override
	public void displayPublicProfile(T account) {
		view.displayPublicProfile(account);
	}
	
	/*
	 * Send message
	 * @see com.ziplly.app.client.activities.AccountPresenter#sendMessage(com.ziplly.app.model.MessageDTO)
	 */
	@Override
	public void sendMessage(ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}
		
		// make sure user is logged in
		if (ctx.getAccount() == null) {
			view.closeSendMessageWidget();
			goTo(new LoginPlace());
			return;
		}
		
		int size = conversation.getMessages().size();
		conversation.getMessages().get(size-1).setSender(ctx.getAccount());
		conversation.setSender(ctx.getAccount());
		dispatcher.execute(new SendMessageAction(conversation), new DispatcherCallbackAsync<SendMessageResult>() {
			@Override
			public void onSuccess(SendMessageResult result) {
				view.displayAccountUpdateSuccessfullMessage();
			}
		});
//		System.out.println("sending message to: "+conversation.getReceiver());
	}
}
