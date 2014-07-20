package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.PrintCouponPlace;
import com.ziplly.app.client.view.IAccountView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.coupon.CouponFormWidgetModal;
import com.ziplly.app.client.view.event.AccountDetailsUpdateEvent;
import com.ziplly.app.client.view.event.AccountUpdateEvent;
import com.ziplly.app.client.view.event.LoadingEventEnd;
import com.ziplly.app.client.view.event.LoadingEventStart;
import com.ziplly.app.client.view.event.LogoutEvent;
import com.ziplly.app.client.view.event.TweetNotAvailableEvent;
import com.ziplly.app.client.view.handler.AccountDetailsUpdateEventHandler;
import com.ziplly.app.client.view.handler.AccountUpdateEventHandler;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.CouponDTO;
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
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlResult;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponTransactionResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
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
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateCommentResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public abstract class AbstractAccountActivity<T extends AccountDTO> extends AbstractActivity implements
    AccountPresenter<T>,
    TweetPresenter,
    EmailPresenter {

	protected AccountNotificationHandler accountNotificationHandler =
	    new AccountNotificationHandler();
	protected int TWEETS_PER_PAGE = 5;
	protected IAccountView<T> view;

	protected List<TweetDTO> lastTweetList;
  protected TweetViewBinder binder;
  
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
			    public void onSuccess(CheckBuyerEligibilityForCouponResult result) {
				    widget.initiatePay(result.getJwtToken());
			    }

		    });
	}

	public void printCoupon(long orderId, long couponId) {
		goTo(new PrintCouponPlace(orderId, couponId));
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
	public void getCouponFormActionUrl(final CouponFormWidgetModal couponFormWidget) {
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
			public void onSuccess(CommentResult result) {
				view.displayModalMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				view.addComment(result.getComment());
			}
		});
	}

//	@Override
//	public void purchaseCoupon(String transactionId,
//	    PurchasedCouponAction.ResultStatus resultStatus,
//	    final CouponDTO coupon) {
//		PurchasedCouponAction action = new PurchasedCouponAction();
//		action.setCoupon(coupon);
//		action.setBuyer(ctx.getAccount());
//		action.setCouponTransactionId(transactionId);
//		action.setResultStatus(resultStatus);
//		dispatcher.execute(action, new DispatcherCallbackAsync<PurchaseCouponResult>() {
//
//			@Override
//			public void onSuccess(PurchaseCouponResult result) {
//				String displayMessage = StringConstants.COUPON_PURCHASE_SUCCESS;
//				AlertType alertType = AlertType.SUCCESS;
//
//				switch (result.getCouponTransaction().getStatus()) {
//					case FAILURE:
//						displayMessage = StringConstants.COUPON_PURCHASE_FAILED;
//						alertType = AlertType.ERROR;
//						break;
//					default:
//						break;
//				}
//				view.displayMessage(displayMessage, alertType);
//			}
//		});
//	}

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
			    public void onSuccess(UpdateCommentResult result) {
				    view.displayModalMessage(StringConstants.COMMENT_UPDATED, AlertType.SUCCESS);
				    view.updateComment(result.getComment());
			    }
		    });
	}

	@Override
	public void getPurchasedCoupons(int start, int pageSize) {
		GetCouponTransactionAction action = new GetCouponTransactionAction();
		action.setStart(start);
		action.setPageSize(pageSize);

		dispatcher.execute(action, new DispatcherCallbackAsync<GetCouponTransactionResult>() {

			@Override
			public void onSuccess(GetCouponTransactionResult result) {
				view.displayPurchasedCoupons(result.getPurchasedCoupons());
				view.setCouponTransactionCount(result.getTotalTransactions());
			}
		});
	}

	public void getCouponQRCodeUrl(long ordersId, long couponId) {
		GetCouponQRCodeUrlAction action = new GetCouponQRCodeUrlAction();
		action.setOrderId(ordersId);
		action.setCouponId(couponId);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetCouponQRCodeUrlResult>() {

			@Override
			public void onSuccess(GetCouponQRCodeUrlResult result) {
				view.displayQrCode(result.getUrl());
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
			public void onSuccess(GetTweetForUserResult result) {
				view.displayTweets(result.getTweets(), displayNoTweetsMessage);
			}

			@Override
			public void onFailure(Throwable th) {
				super.onFailure(th);
				stopThreads();
				eventBus.fireEvent(new LoadingEventEnd());
				eventBus.fireEvent(new TweetNotAvailableEvent());
			}
		});
	}

	protected void getAccountNotifications() {
		dispatcher.execute(new GetAccountNotificationAction(), accountNotificationHandler);
	}

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

  @Override
  public void cancelTransaction(long transactionId) {
    Window.alert("Cancelled transaction: "+transactionId);
  }
	
  public void displayMap(NeighborhoodDTO n) {
    view.displayMap(n.getPostalCodes().get(0).toString());
  }

  protected void stopThreads() {
    if (binder != null) {
      binder.stop();
    }
  }

  protected abstract void onAccountDetailsUpdate();
  
  public abstract DispatcherCallbackAsync<TweetResult> getTweetHandler();
  
  public abstract void displayAccontUpdate();
}
