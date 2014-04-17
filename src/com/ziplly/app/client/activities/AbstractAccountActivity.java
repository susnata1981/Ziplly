package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.exceptions.AccessError;
import com.ziplly.app.client.exceptions.DuplicateException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.NotSharedError;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.ImageUtil;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.widget.CouponFormWidget;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.client.widget.blocks.FormUploadWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponResult;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CommentResult;
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteImageResult;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.DeleteTweetResult;
import com.ziplly.app.shared.EmailTemplate;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountDetailsResult;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetForUserResult;
import com.ziplly.app.shared.LikeResult;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.LogoutResult;
import com.ziplly.app.shared.PurchaseCouponResult;
import com.ziplly.app.shared.PurchasedCouponAction;
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
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateCommentResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public abstract class AbstractAccountActivity<T extends AccountDTO> extends AbstractActivity implements
    AccountPresenter<T>, TweetPresenter, EmailPresenter {
	
	protected AccountNotificationHandler accountNotificationHandler =
	    new AccountNotificationHandler();
	protected int TWEETS_PER_PAGE = 5;
	protected IAccountView<T> view;

	public AbstractAccountActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    IAccountView<T> view) {
		super(dispatcher, eventBus, placeController, ctx);
		this.view = view;
		setupHandlers();
	}

	@Override
	public void checkCouponPurchaseEligibility(final CouponDTO coupon, final TweetWidget widget) {
		CheckBuyerEligibilityForCouponAction eligibilityAction =
		    new CheckBuyerEligibilityForCouponAction();
		eligibilityAction.setCoupon(coupon);

		dispatcher.execute(
		    eligibilityAction,
		    new DispatcherCallbackAsync<CheckBuyerEligibilityForCouponResult>() {

			    @Override
			    public void onFailure(Throwable th) {
				    view.displayMessage(StringConstants.FAILED_TO_BUY_COUPON, AlertType.ERROR);
			    }

			    @Override
			    public void onSuccess(CheckBuyerEligibilityForCouponResult result) {
				    Window.alert("Eligible for buy...");
				    widget.initiatePay();
			    }

		    });
	}

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

	@Override
	public void deleteTweet(final TweetDTO tweet) {
		dispatcher.execute(
		    new DeleteTweetAction(tweet.getTweetId()),
		    new DispatcherCallbackAsync<DeleteTweetResult>() {
			    @Override
			    public void onFailure(Throwable th) {
				    if (th instanceof AccessError) {
					    view.displayModalMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				    } else {
					    view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				    }
			    }

			    @Override
			    public void onSuccess(DeleteTweetResult result) {
				    view.displayModalMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
				    view.removeTweet(tweet);
			    }
		    });
	}

	@Override
	public void displayMessage(String message, AlertType type) {
		view.displayModalMessage(message, type);
	}

	public void getAccountDetails(DispatcherCallbackAsync<GetAccountDetailsResult> callback) {
		dispatcher.execute(new GetAccountDetailsAction(), callback);
	}

	@Deprecated
	@Override
	public void getCouponFormActionUrl(final CouponFormWidget couponFormWidget) {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    couponFormWidget.setFormUploadActionUrl(result.getImageUrl());
			    }
		    });
	}

	// TODO (combine this with getAccountDetails)
	public void getPublicAccountDetails(Long accountId,
	    DispatcherCallbackAsync<GetAccountDetailsResult> callback) {
		dispatcher.execute(new GetPublicAccountDetailsAction(accountId), callback);
	}

	@Override
	public SendMessagePresenter getSendMessagePresenter() {
		return this;
	}

	public abstract DispatcherCallbackAsync<TweetResult> getTweetHandler();

	@Override
	public void initializeUploadForm(final FormUploadWidget formUploadWidget) {
		setFormUploadActionUrl(formUploadWidget);

		formUploadWidget.setUploadFormSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				try {
					if (event.getResults() != null) {
						ImageDTO imageDto = ImageUtil.parseImageUrl(event.getResults());
						formUploadWidget.displayImagePreview(imageDto.getUrl());
						setFormUploadActionUrl(formUploadWidget);
					}
				} catch (Error error) {
					view.displayMessage(StringConstants.INVALID_IMAGE, AlertType.ERROR);
					setFormUploadActionUrl(formUploadWidget);
				}
			}

		});
	}

	@Override
	public void invitePeople(List<String> emails) {
		SendEmailAction action = new SendEmailAction();
		action.setEmailTemplate(EmailTemplate.INVITE_PEOPLE);
		action.setEmailList(emails);
		dispatcher.execute(action, new DispatcherCallbackAsync<SendEmailResult>() {
			@Override
			public void onSuccess(SendEmailResult result) {
				view.displayModalMessage(StringConstants.EMAIL_SENT, AlertType.SUCCESS);
			}
		});
	}

	@Override
	public void likeTweet(Long tweetId) {
		LikeTweetAction action = new LikeTweetAction();
		action.setTweetId(tweetId);
		dispatcher.execute(action, new DispatcherCallbackAsync<LikeResult>() {

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof DuplicateException) {
					view.displayModalMessage(StringConstants.OPERATION_FAILED, AlertType.ERROR);
				}
			}

			@Override
			public void onSuccess(LikeResult result) {
				view.displayModalMessage(StringConstants.LIKE_SAVED, AlertType.SUCCESS);
				view.updateTweetLike(result.getLike());
			}
		});
	}

	@Override
	public void logout() {
		dispatcher.execute(
		    new LogoutAction(ctx.getAccount().getUid()),
		    new DispatcherCallbackAsync<LogoutResult>() {
			    @Override
			    public void onFailure(Throwable th) {
				    System.out.println("Failed to logout" + th);
			    }

			    @Override
			    public void onSuccess(LogoutResult result) {
				    eventBus.fireEvent(new LogoutEvent());
				    ctx.setAccount(null);
				    goTo(new LoginPlace());
			    }
		    });
	}

	@Override
	public void messagesLinkClicked() {
		placeController.goTo(new ConversationPlace());
	}

	@Override
	public void postComment(final CommentDTO comment) {
		dispatcher.execute(new CommentAction(comment), new DispatcherCallbackAsync<CommentResult>() {
			@Override
			public void onFailure(Throwable caught) {
				view.displayModalMessage(StringConstants.FAILED_TO_UPDATE_COMMENT, AlertType.ERROR);
			}

			@Override
			public void onSuccess(CommentResult result) {
				view.displayModalMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				view.addComment(result.getComment());
			}
		});
	}

	@Override
	public void purchaseCoupon(final CouponDTO coupon) {
		PurchasedCouponAction action = new PurchasedCouponAction();
		action.setCoupon(coupon);
		action.setBuyer(ctx.getAccount());
		dispatcher.execute(action, new DispatcherCallbackAsync<PurchaseCouponResult>() {

			@Override
			public void onFailure(Throwable th) {
				Window.alert(th.getLocalizedMessage());
			}

			@Override
			public void onSuccess(PurchaseCouponResult result) {
				Window.alert("success");
			}
		});
	}

	public void reportSpam(SpamDTO spam, DispatcherCallbackAsync<ReportSpamResult> handler) {
		if (spam == null) {
			throw new IllegalArgumentException();
		}
		spam.setReporter(ctx.getAccount());
		dispatcher.execute(new ReportSpamAction(spam), handler);
	}

	@Override
	public void save(T account) {
		dispatcher.execute(
		    new UpdateAccountAction(account),
		    new DispatcherCallbackAsync<UpdateAccountResult>() {
			    @Override
			    public void onFailure(Throwable error) {
				    view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			    }

			    @Override
			    public void onSuccess(UpdateAccountResult result) {
				    view.displayModalMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL, AlertType.SUCCESS);
				    eventBus.fireEvent(new AccountUpdateEvent(result.getAccount()));
			    }
		    });
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
		conversation.getMessages().get(size - 1).setSender(ctx.getAccount());
		conversation.setSender(ctx.getAccount());
		dispatcher.execute(
		    new SendMessageAction(conversation),
		    new DispatcherCallbackAsync<SendMessageResult>() {
			    @Override
			    public void onFailure(Throwable th) {
				    view.displayModalMessage(StringConstants.MESSAGE_NOT_DELIVERED, AlertType.ERROR);
			    }

			    @Override
			    public void onSuccess(SendMessageResult result) {
				    view.displayModalMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
			    }
		    });
	};

	@Override
	public void sendTweet(TweetDTO tweet) {
		// TODO(shaan): do we need this?
		AccountDTO account = ctx.getAccount();
		if (account == null) {
			placeController.goTo(new LoginPlace());
			return;
		}
		dispatcher.execute(new TweetAction(tweet), getTweetHandler());
	}

	public void setImageUploadUrl() {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    view.setImageUploadUrl(result.getImageUrl());
			    }
		    });
	}
	
	public void setUploadImageHandler() {
		view.addUploadFormHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				view.displayProfileImagePreview(imageUrl);
				view.resetImageUploadUrl();
				setImageUploadUrl();
			}
		});
	}

	@Override
	public void updateComment(CommentDTO comment) {
		dispatcher.execute(
		    new UpdateCommentAction(comment),
		    new DispatcherCallbackAsync<UpdateCommentResult>() {

			    @Override
			    public void onFailure(Throwable th) {
				    if (th instanceof AccessError) {
					    view.displayModalMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
				    } else {
					    view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				    }
			    }

			    @Override
			    public void onSuccess(UpdateCommentResult result) {
				    view.displayModalMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				    view.updateComment(result.getComment());
			    }
		    });
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null) {
			return;
		}

		dispatcher.execute(
		    new UpdateTweetAction(tweet),
		    new DispatcherCallbackAsync<UpdateTweetResult>() {
			    @Override
			    public void onFailure(Throwable caught) {
				    if (caught instanceof AccessError) {
					    view.displayModalMessage(StringConstants.OPERATION_FAILED, AlertType.ERROR);
					    return;
				    }
				    view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
			    }

			    @Override
			    public void onSuccess(UpdateTweetResult result) {
				    view.updateTweet(result.getTweet());
				    view.displayModalMessage(StringConstants.TWEET_UPDATED, AlertType.SUCCESS);
			    }
		    });
	}

	protected void fetchTweets(long accountId,
	    int page,
	    int pageSize,
	    final boolean displayNoTweetsMessage) {
		eventBus.fireEvent(new LoadingEventStart());
		GetTweetForUserAction action = new GetTweetForUserAction(accountId, page, pageSize);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetTweetForUserResult>() {
			@Override
			public void onFailure(Throwable th) {
				if (th instanceof NotSharedError) {
					view.displayTweetViewMessage(StringConstants.TWEET_NOT_SHARED, AlertType.WARNING);
				} else if (th instanceof NotFoundException) {
					// view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
				} else {
					view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
				}
				stopThreads();
				eventBus.fireEvent(new LoadingEventEnd());
				eventBus.fireEvent(new TweetNotAvailableEvent());
			}

			@Override
			public void onSuccess(GetTweetForUserResult result) {
				view.displayTweets(result.getTweets(), displayNoTweetsMessage);
			}
		});
	}

	protected void getAccountNotifications() {
		dispatcher.execute(new GetAccountNotificationAction(), accountNotificationHandler);
	}

	protected abstract void onAccountDetailsUpdate();

	@Override
	protected void setupHandlers() {
		super.setupHandlers();
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

	void displayMap(NeighborhoodDTO n) {
		view.displayMap(n.getPostalCodes().get(0).toString());
	}

	@Deprecated
	void getLatLng(AccountDTO account, DispatcherCallbackAsync<GetLatLngResult> handler) {
		GetLatLngAction action = new GetLatLngAction();
		action.setAccount(account);
		dispatcher.execute(action, handler);
	}

	abstract void stopThreads();

	private void setFormUploadActionUrl(final FormUploadWidget formUploadWidget) {
		dispatcher.execute(
		    new GetImageUploadUrlAction(),
		    new DispatcherCallbackAsync<GetImageUploadUrlResult>() {

			    @Override
			    public void onSuccess(GetImageUploadUrlResult result) {
				    if (result.getImageUrl() != null) {
					    formUploadWidget.setUploadFormActionUrl(result.getImageUrl());
					    formUploadWidget.enableUploadButton();
				    }
			    }
		    });
	}
}
