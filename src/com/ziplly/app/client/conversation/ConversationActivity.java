package com.ziplly.app.client.conversation;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AbstractActivity;
import com.ziplly.app.client.activities.DefaultViewLoaderAsyncCallback;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.view.IConversationView;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.ConversationStatus;
import com.ziplly.app.model.ConversationType;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.ViewConversationAction;
import com.ziplly.app.shared.ViewConversationResult;

public class ConversationActivity extends AbstractActivity implements
    ConversationView.ConversationViewPresenter {
	private IConversationView view;
	private AcceptsOneWidget panel;
//	private GetConversationHandler handler = new GetConversationHandler();
//	private SingleConversationHandler singleConversationHandler = new SingleConversationHandler();
	private ConversationPlace place;
	private AsyncProvider<ConversationView> viewProvider;

	@Inject
	public ConversationActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    ConversationPlace place,
	    AsyncProvider<ConversationView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<ConversationView>() {

			@Override
			public void onSuccess(ConversationView result) {
				ConversationActivity.this.view = result;
				bind();
				go(ConversationActivity.this.panel);
				setupHandlers();
				internalStart();
			}
			
		});
	}

	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {
			@Override
			public void onEvent(AccountDetailsUpdateEvent event) {
				updateMessageCount(event.getAccountDetails());
			}
		});

		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				internalStart();
				getAccountDetails();
			}
		});
	}

	/**
	 * Updated unread message count.
	 */
	private void updateMessageCount(GetAccountDetailsResult result) {
		view.setUnreadMessageCount(result.getUnreadMessages());
	}

	private void internalStart() {
		if (place.getConversationId() != 0) {
			GetConversationsAction action = new GetConversationsAction();
			action.setType(ConversationType.SINGLE);
			action.setConversationId(place.getConversationId());
			dispatcher.execute(action, new SingleConversationHandler(eventBus));
		} else {
			GetConversationsAction action = new GetConversationsAction();
			action.setType(ConversationType.RECEIVED);
			action.setStart(0);
			action.setPageSize(0);
			action.setGetTotalConversation(true);
			dispatcher.execute(action, new GetConversationHandler(eventBus));
		}
	}

	@Override
	public void getConversations(GetConversationsAction action) {
		dispatcher.execute(action, new GetConversationHandler(eventBus));
	}

	private void getAccountDetails() {
		dispatcher.execute(
		    new GetAccountDetailsAction(),
		    new DispatcherCallbackAsync<GetAccountDetailsResult>(eventBus) {

			    @Override
			    public void onSuccess(GetAccountDetailsResult result) {
				    eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
			    }
		    });
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

	@Override
	public void sendMessage(final ConversationDTO conversation) {
		if (conversation == null) {
			throw new IllegalArgumentException();
		}

		dispatcher.execute(
		    new SendMessageAction(conversation),
		    new DispatcherCallbackAsync<SendMessageResult>(eventBus) {
			    @Override
			    public void onSuccess(SendMessageResult result) {
				    view.updateConversation(conversation);
			    }
		    });
	}

	/**
	 * Called when a conversation is viewed
	 */
	@Override
	public void onView(ConversationDTO conversation) {
		if (conversation != null) {
			// update the status column only if it's the receiver
			if (conversation.getStatus() == ConversationStatus.READ) {
				return;
			}

			dispatcher.execute(
			    new ViewConversationAction(conversation.getId()),
			    new DispatcherCallbackAsync<ViewConversationResult>(eventBus) {
				    @Override
				    public void onSuccess(ViewConversationResult result) {
					    updateAccountDetails();
				    }
			    });
		}
	}

	private void updateAccountDetails() {
		dispatcher.execute(
		    new GetAccountDetailsAction(),
		    new DispatcherCallbackAsync<GetAccountDetailsResult>(eventBus) {
			    @Override
			    public void onSuccess(GetAccountDetailsResult result) {
				    eventBus.fireEvent(new AccountDetailsUpdateEvent(result));
			    }
		    });
	}

	private class GetConversationHandler extends DispatcherCallbackAsync<GetConversationsResult> {
	  
	  public GetConversationHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(GetConversationsResult result) {
			if (result.getTotalConversations() != null) {
				view.setTotalConversation(result.getTotalConversations());
			}
			view.displayConversations(result.getConversations());
		}
//
//		@Override
//		public void onFailure(Throwable th) {
//			view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
//		}
	};

	private class SingleConversationHandler extends DispatcherCallbackAsync<GetConversationsResult> {
	  public SingleConversationHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(GetConversationsResult result) {
			if (result.getConversations().size() == 1) {
				view.displayConversation(result.getConversations().get(0));
			}
		}
	}

	@Override
	public AccountDTO getAccount() {
		return ctx.getAccount();
	}

  @Override
  public String mayStop() {
    return null;
  }

  @Override
  public void onCancel() {
  };
}
