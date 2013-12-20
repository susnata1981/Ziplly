package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.SendEmailAction;
import com.ziplly.app.shared.SendEmailResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.TweetResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public abstract class AbstractAccountActivity<T extends AccountDTO> extends AbstractActivity implements AccountPresenter<T>, EmailPresenter {
	protected IAccountView<T> view;
	
	public AbstractAccountActivity(CachingDispatcherAsync dispatcher,
			EventBus eventBus, 
			PlaceController placeController,
			ApplicationContext ctx,
			IAccountView<T> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		
		setupEventHandlers();
	}

	private void setupEventHandlers() {
		eventBus.addHandler(AccountUpdateEvent.TYPE, new AccountUpdateEventHandler() {
			@Override
			public void onEvent(AccountUpdateEvent event) {
				AbstractAccountActivity.this.ctx.setAccount(event.getAccount());
				displayProfile();
			}
		});
		
		eventBus.addHandler(AccountDetailsUpdateEvent.TYPE, new AccountDetailsUpdateEventHandler() {
			@Override
			public void onEvent(AccountDetailsUpdateEvent event) {
				onAccountDetailsUpdate();
			}
		});
	}

	protected abstract void onAccountDetailsUpdate();
	
	@Override
	public void fetchData() {
	}
	
	@Override
	public void save(T account) {
		dispatcher.execute(new UpdateAccountAction(account),
				new DispatcherCallbackAsync<UpdateAccountResult>() {
					@Override
					public void onSuccess(UpdateAccountResult result) {
						view.displayMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
						eventBus.fireEvent(new AccountUpdateEvent(result
								.getAccount()));
					}

					public void onFailure(Throwable error) {
						view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
					}
				});
	}
	
	@Override
	public void sendTweet(TweetDTO tweet) {
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
		}
		dispatcher.execute(new TweetAction(tweet), getTweetHandler());
	}

	public abstract DispatcherCallbackAsync<TweetResult> getTweetHandler();
	
	@Override
	public void postComment(final CommentDTO comment) {
		dispatcher.execute(new CommentAction(comment), new DispatcherCallbackAsync<CommentResult>() {
			@Override
			public void onSuccess(CommentResult result) {
				view.displayMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				view.updateComment(comment);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.displayMessage(StringConstants.FAILED_TO_UPDATE_COMMENT, AlertType.ERROR);
			}
		});
	}
	
	@Override
	public void likeTweet(Long tweetId) {
		LikeTweetAction action = new LikeTweetAction();
		action.setTweetId(tweetId);
		dispatcher.execute(action, new DispatcherCallbackAsync<LikeResult>() {

			@Override
			public void onSuccess(LikeResult result) {
				view.displayMessage(StringConstants.LIKE_SAVED, AlertType.SUCCESS);
				view.updateTweetLike(result.getLike());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof DuplicateException) {
					view.displayMessage(StringConstants.OPERATION_FAILED, AlertType.ERROR);
				}
			}
		});
	}
	
	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null) {
			// do nothing
			return;
		}
		
		dispatcher.execute(new UpdateTweetAction(tweet), new DispatcherCallbackAsync<UpdateTweetResult>() {
			@Override
			public void onSuccess(UpdateTweetResult result) {
				view.updateTweet(result.getTweet());
				view.displayMessage(StringConstants.TWEET_UPDATED, AlertType.SUCCESS);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof AccessError) {
					view.displayMessage(StringConstants.OPERATION_FAILED, AlertType.ERROR);
					return;
				} 
				view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			}
		});
	}
	
	@Override
	public void deleteTweet(TweetDTO tweet) {
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
					
					public void onFailure(Throwable th) {
						System.out.println("Failed to logout"+th);
					}
				});
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
	
	void fetchTweets(long accountId, int page, int pageSize) {
		GetTweetForUserAction action = new GetTweetForUserAction(accountId, page, pageSize);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetTweetForUserResult>() {
			@Override
			public void onSuccess(GetTweetForUserResult result) {
				view.displayTweets(result.getTweets());
			}
		});
	}
	
	@Override
	public void messagesLinkClicked() {
		placeController.goTo(new ConversationPlace());
	}
	
	void getLatLng(AccountDTO account, DispatcherCallbackAsync<GetLatLngResult> handler) {
		GetLatLngAction action = new GetLatLngAction();
		action.setAccount(account);
		dispatcher.execute(action, handler);
	}

	public void getAccountDetails(DispatcherCallbackAsync<GetAccountDetailsResult> callback) {
		dispatcher.execute(new GetAccountDetailsAction(), callback); 
	}
	
	public void getPublicAccountDetails(Long accountId, DispatcherCallbackAsync<GetAccountDetailsResult> callback) {
		dispatcher.execute(new GetPublicAccountDetailsAction(accountId), callback); 
	}
	
	@Override
	public void invitePeople(List<String> emails){
		System.out.println("Email people..."+emails);
		SendEmailAction action = new SendEmailAction();
		action.setEmailTemplate(EmailTemplate.INVITE_PEOPLE);
		action.setEmailList(emails);
		dispatcher.execute(action, new DispatcherCallbackAsync<SendEmailResult>() {
			@Override
			public void onSuccess(SendEmailResult result) {
				view.displayMessage(StringConstants.EMAIL_SENT, AlertType.SUCCESS);
			}
		});
	};
	
	public void reportSpam(SpamDTO spam, DispatcherCallbackAsync<ReportSpamResult> handler) {
		if (spam == null) {
			throw new IllegalArgumentException();
		}
		spam.setReporter(ctx.getAccount());
		dispatcher.execute(new ReportSpamAction(spam), handler);
	}
	
	public TweetWidget getTweetWidget() {
		return ctx.getTweetWidget();
	}
	
	public void displayMessage(String message, AlertType type) {
		view.displayMessage(message, type);
	}
}
