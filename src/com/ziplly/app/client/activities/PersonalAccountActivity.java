package com.ziplly.app.client.activities;


import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.account.AccountView;
import com.ziplly.app.client.view.account.PersonalAccountViewPresenter;

public class PersonalAccountActivity extends AbstractActivity { 
	private AsyncProvider<AccountView> accountView;
  private AcceptsOneWidget panel;
  private PersonalAccountPlace place;
  
	public PersonalAccountActivity(
	    CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    AsyncProvider<AccountView> accountView,
	    PersonalAccountPlace place) {

		super(dispatcher, eventBus, placeController, ctx);
		this.place = place;
		this.accountView = accountView;
	}

	/*
	@Override
	protected void setupHandlers() {
		super.setupHandlers();
		eventBus.addHandler(TweetNotAvailableEvent.TYPE, new TweetNotAvailableEventHandler() {

			@Override
			public void onEvent(TweetNotAvailableEvent event) {
				if (binder != null) {
					binder.stop();
				}
			}
		});
	}

	@Override
	public void bind() {
//		view.setPresenter(this);
//		view.getTweetView().setPresenter(this);
//		view.getTweetWidget().setPresenter(this);
//		view.getEmailWidget().setPresenter(this);
	}

	@Override
	public void go(AcceptsOneWidget container) {
		container.setWidget(view);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		viewProvider.get(new DefaultViewLoaderAsyncCallback<AccountView>() {

			@Override
			public void onSuccess(AccountView result) {
				PersonalAccountActivity.this.view = result;
				bind();
				setupHandlers();
				go(PersonalAccountActivity.this.panel);
				if (place.getAccountId() != 0) {
					displayPublicProfile(place.getAccountId());
				} else if (place.isShowTransactions()) {
					displayProfile();
					view.displayCouponTransactions();
				} else {
					displayProfile();
				}
			}
		});
	}

	
	@Override
	public void displayPublicProfile(final Long accountId) {
		if (accountId != null) {
			dispatcher.execute(new GetAccountByIdAction(accountId), new GetAccountByIdActionHandler(eventBus));
			fetchTweets(place.getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, true);
			startInfiniteScrollThread();
			getPublicAccountDetails(accountId, new GetPublicAccountDetailsActionHandler(eventBus));
		}
	}

	@Override
	public void displayProfile() {
		if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountPlace());
			return;
		}

		view.displayProfile((PersonalAccountDTO) ctx.getAccount());

		// Display target neighborhood
		view.displayTargetNeighborhoods(getTargetNeighborhoodList());

		fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, false);
		startInfiniteScrollThread();
		displayMap(ctx.getCurrentNeighborhood());
		getAccountDetails(new GetAccountDetailsActionHandler(eventBus));
		getAccountNotifications();
		setupImageUpload();
		// Display account updates
		displayAccontUpdate();
	}

	@Override
	public void displayAccontUpdate() {
	  PersonalAccountDTO account = (PersonalAccountDTO) ctx.getAccount();
    List<PendingActionTypes> pendingActions = new ArrayList<PendingActionTypes>();
    
    if (account.getImages().size() == 0 
        || account.getInterests().isEmpty() 
        || FieldVerifier.isEmpty(account.getOccupation())
        || FieldVerifier.isEmpty(account.getIntroduction())) {
      pendingActions.add(PendingActionTypes.INCOMPLETE_ACCOUNT_SETTINGS);
    }
    
    view.displayAccontUpdate(pendingActions);
  }

  private void setupImageUpload() {
		setImageUploadUrl();
		setUploadImageHandler();
	}

	@Override
	public void settingsLinkClicked() {
		placeController.goTo(new PersonalAccountSettingsPlace());
	}

	
	@Override
	public boolean hasMoreElements() {
		if (lastTweetList == null) {
			return true;
		}
		return lastTweetList.size() == TWEETS_PER_PAGE;
	}

	@Override
	public void onScrollBottomHit() {
		tweetPageIndex++;
		GetTweetForUserAction action = null;
		if (place.getAccountId() != 0) {
			action = new GetTweetForUserAction(place.getAccountId(), tweetPageIndex, TWEETS_PER_PAGE);
		} else if (ctx.getAccount() != null) {
			action =
			    new GetTweetForUserAction(
			        ctx.getAccount().getAccountId(),
			        tweetPageIndex,
			        TWEETS_PER_PAGE);
		}

		dispatcher.execute(action, scrollBottomHitActionHandler);
	}

	@Override
	public void reportTweetAsSpam(TweetDTO tweet) {
		SpamDTO spam = new SpamDTO();
		spam.setTweet(tweet);
		spam.setReporter(ctx.getAccount());
		reportSpam(spam, new ReportSpamActionHandler(eventBus));
	}

	protected void onAccountDetailsUpdate(GetAccountDetailsResult result) {
		ctx.setAccountDetails(result);
		if (view != null) {
			view.updateAccountDetails(ctx);
		}
	}

	@Override
	protected void onAccountDetailsUpdate() {
		getAccountDetails(new DispatcherCallbackAsync<GetAccountDetailsResult>(eventBus) {
			@Override
			public void onSuccess(GetAccountDetailsResult result) {
				onAccountDetailsUpdate(result);
			}
		});
	}

	@Override
	public void onStop() {
		if (binder != null) {
			binder.stop();
		}
		eventBus.fireEvent(new LoadingEventEnd());
		view.clear();
	}

	private class GetAccountByIdActionHandler extends DispatcherCallbackAsync<GetAccountByIdResult> {

	  public GetAccountByIdActionHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(GetAccountByIdResult result) {
			AccountDTO account = result.getAccount();
			if (account instanceof PersonalAccountDTO) {
				view.displayPublicProfile((PersonalAccountDTO) account);
				displayMap(result.getAccount().getLocations().get(0).getNeighborhood());
			} else {
				// take some action here
				placeController.goTo(new BusinessAccountPlace(account.getAccountId()));
			}
		}

		@Override
		public void onFailure(Throwable th) {
			super.onFailure(th);
			view.displayProfileSection(false);
			view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
		}
	}

	private class GetAccountDetailsActionHandler extends
	    DispatcherCallbackAsync<GetAccountDetailsResult> {

	  public GetAccountDetailsActionHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			onAccountDetailsUpdate(result);
		}
	}

	@Override
	public DispatcherCallbackAsync<TweetResult> getTweetHandler() {
		return new TweetHandler(eventBus);
	}

	@Override
	public void deleteTweet(final TweetDTO tweet) {
		if (ctx.getAccount().getAccountId() != tweet.getSender().getAccountId()) {
			view.displayModalMessage(StringConstants.INVALID_ACCESS, AlertType.ERROR);
		}

		dispatcher.execute(
		    new DeleteTweetAction(tweet.getTweetId()),
		    new DispatcherCallbackAsync<DeleteTweetResult>(eventBus) {
			    @Override
			    public void onSuccess(DeleteTweetResult result) {
				    view.displayModalMessage(StringConstants.TWEET_REMOVED, AlertType.SUCCESS);
				    view.removeTweet(tweet);
			    }
		    });
	}

	private class GetPublicAccountDetailsActionHandler extends
	    DispatcherCallbackAsync<GetAccountDetailsResult> {

	  public GetPublicAccountDetailsActionHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
		@Override
		public void onSuccess(GetAccountDetailsResult result) {
			// onAccountDetailsUpdate(result);
			view.updatePublicAccountDetails(result);
		}
	}

	private class TweetHandler extends DispatcherCallbackAsync<TweetResult> {
		
	  public TweetHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
	  @Override
		public void onSuccess(TweetResult result) {
			placeController.goTo(new HomePlace());
			view.clearTweet();
		}

		@Override
		public void onFailure(Throwable th) {
			view.displayModalMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
		}
	}

	private class ReportSpamActionHandler extends DispatcherCallbackAsync<ReportSpamResult> {
		
	  ReportSpamActionHandler(EventBus eventBus) {
	    super(eventBus);
	  }
	  
	  @Override
		public void onSuccess(ReportSpamResult result) {
			view.displayModalMessage(StringConstants.REPORT_SPAM_SUCCESSFUL, AlertType.SUCCESS);
		}
	}

	 void startInfiniteScrollThread() {
    if (binder != null) {
      binder.stop();
    }
    binder = new TweetViewBinder(view.getTweetSectionElement(), this);
    binder.start();
  }
	   
	private class ScrollBottomHitActionHandler extends DispatcherCallbackAsync<GetTweetForUserResult> {
	  
	  public ScrollBottomHitActionHandler(EventBus eventBus) {
	    super(eventBus);
	  }
	  
		@Override
		public void onSuccess(GetTweetForUserResult result) {
			lastTweetList = result.getTweets();
			view.addTweets(result.getTweets());
		}
	}
*/

  @Override
  public String mayStop() {
    return null;
  }

  @Override
  public void onCancel() {
  }

  @Override
  public void onStop() {
  }

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }

  @Override
  protected void doStart() {
    accountView.get(new DefaultViewLoaderAsyncCallback<AccountView>() {

      @Override
      public void onSuccess(AccountView accountView) {
        PersonalAccountActivity.this.panel.setWidget(accountView);
        new PersonalAccountViewPresenter(dispatcher, eventBus, ctx, accountView, PersonalAccountActivity.this.panel, place);
//        bind();
//        setupHandlers();
//        go(PersonalAccountActivity.this.panel);
//        if (place.getAccountId() != 0) {
//          displayPublicProfile(place.getAccountId());
//        } else if (place.isShowTransactions()) {
//          displayProfile();
//          view.displayCouponTransactions();
//        } else {
//          displayProfile();
//        }
      }
    });
  }
}
