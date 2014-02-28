package com.ziplly.app.client.activities;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.ApplicationContext.Environment;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.AdminView;
import com.ziplly.app.client.view.AdminView.AdminPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSearchCriteria;
import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.Role;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;
import com.ziplly.app.shared.CreateNeighborhoodAction;
import com.ziplly.app.shared.CreateNeighborhoodResult;
import com.ziplly.app.shared.CreateRegistrationAction;
import com.ziplly.app.shared.CreateRegistrationResult;
import com.ziplly.app.shared.DeleteNeighborhoodAction;
import com.ziplly.app.shared.DeleteNeighborhoodResult;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.GetTweetsResult;
import com.ziplly.app.shared.SearchAccountAction;
import com.ziplly.app.shared.SearchAccountResult;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountResult;
import com.ziplly.app.shared.UpdateNeighborhoodAction;
import com.ziplly.app.shared.UpdateNeighborhoodResult;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.UpdateTweetResult;

public class AdminActivity extends AbstractActivity implements AdminPresenter {

	private AdminView view;
	private AsyncProvider<AdminView> viewProvider;
	private AcceptsOneWidget panel;

	public AdminActivity(CachingDispatcherAsync dispatcher, EventBus eventBus,
			PlaceController placeController, ApplicationContext ctx,
			AsyncProvider<AdminView> viewProvider) {

		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		checkAccountLogin();
	}

	@Override
	public void doStart() {
		if (ctx.getAccount().getRole() != Role.ADMINISTRATOR) {
			Window.alert("Invalid access");
			placeController.goTo(new PersonalAccountPlace());
			return;
		}
		viewProvider.get(new AsyncCallback<AdminView>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(AdminView result) {
				AdminActivity.this.view = result;
				bind();
				setUploadFormActionUrl();
				setImageUploadFormSubmitCompleteHandler();
			}
		});
	}

	@Override
	public void onStop() {
		view.clear();
	}

	@Override
	public void bind() {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	@Override
	public void searchTweets(final int start, int end, TweetSearchCriteria tsc) {
		GetTweetsAction action = new GetTweetsAction();
		action.setCriteria(tsc);
		action.setStart(start);
		action.setEnd(end);
		dispatcher.execute(action, new DispatcherCallbackAsync<GetTweetsResult>() {
			@Override
			public void onSuccess(GetTweetsResult result) {
				if (result != null) {
					view.setTweetData(start, result.getTweets());
					view.setTweetRowCount(result.getTotalTweetCount().intValue());
				}
			}
		});
	}

	@Override
	public void update(TweetDTO tweet) {
		dispatcher.execute(new UpdateTweetAction(tweet),
				new DispatcherCallbackAsync<UpdateTweetResult>() {
					@Override
					public void onSuccess(UpdateTweetResult result) {
						view.refresh();
					}
				});
	}

	@Override
	public void update(AccountDTO account) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchAccounts(final int start, int end, AccountSearchCriteria asc) {
		SearchAccountAction action = new SearchAccountAction(asc);
		action.setStart(start);
		action.setEnd(end);
		action.setCriteria(asc);
		dispatcher.execute(action, new DispatcherCallbackAsync<SearchAccountResult>() {
			@Override
			public void onSuccess(SearchAccountResult result) {
				if (result != null) {
					view.setAccountData(start, result.getAccounts());
					view.setAccountRowCount(result.getTotalAccounts());
				}
			}
		});
	}

	@Override
	public void go(AcceptsOneWidget container) {
	}

	@Override
	public void inviteForRegistration(String email, AccountType type, BusinessType btype) {
		if (email != null) {
			dispatcher.execute(new CreateRegistrationAction(email, type, btype),
					new DispatcherCallbackAsync<CreateRegistrationResult>() {
						@Override
						public void onSuccess(CreateRegistrationResult result) {
							view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
						}

						public void onFailure(Throwable th) {
							view.displayMessage(StringConstants.INTERNAL_ERROR, AlertType.ERROR);
						}
					});
		}
	}

	@Override
	public void updateAccount(AccountDTO account) {
		if (account != null) {
			dispatcher.execute(new UpdateAccountAction(account),
					new DispatcherCallbackAsync<UpdateAccountResult>() {
						@Override
						public void onSuccess(UpdateAccountResult result) {
							view.displayMessage(StringConstants.ACCOUNT_SAVE_SUCCESSFUL,
									AlertType.SUCCESS);
						}
					});
		}
	}

	@Override
	public void searchNeighborhoods(final GetNeighborhoodAction action) {
		dispatcher.execute(action, new DispatcherCallbackAsync<GetNeighborhoodResult>() {

			@Override
			public void onSuccess(GetNeighborhoodResult result) {
				view.setNeighborhoodData(action.getStart(), result.getNeighbordhoods());
			}
		});
	}

	@Override
	public void updateNeighborhood(NeighborhoodDTO neighborhood) {
		if (neighborhood != null) {
			dispatcher.execute(new UpdateNeighborhoodAction(neighborhood),
					new DispatcherCallbackAsync<UpdateNeighborhoodResult>() {
						@Override
						public void onSuccess(UpdateNeighborhoodResult result) {
							view.displayMessage(StringConstants.SAVE_SUCCESSFULL, AlertType.SUCCESS);
						}

						@Override
						public void onFailure(Throwable th) {
							view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
						}
					});
		}
	}

	public void setUploadFormActionUrl() {
		dispatcher.execute(new GetImageUploadUrlAction(),
				new DispatcherCallbackAsync<GetImageUploadUrlResult>() {
					@Override
					public void onSuccess(GetImageUploadUrlResult result) {
						if (ctx.getEnvironment() == Environment.DEVEL) {
							String url = result.getImageUrl().replace(
									"susnatas-MacBook-Pro.local:8888", "127.0.0.1:8888");
							System.out.println("Setting upload image form action to:" + url);
							view.setUploadFormActionUrl(url);
						} else {
							view.setUploadFormActionUrl(result.getImageUrl());
						}
					}
				});
	}
	
	public void setImageUploadFormSubmitCompleteHandler() {
		view.setUploadFormSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String imageUrl = event.getResults();
				System.out.println("Received uploaded image url:"+imageUrl);
				view.displayImagePreview(imageUrl);
				view.resetNeighborhoodImageUploadForm();
				setUploadFormActionUrl();
			}
		});
	}

	@Override
	public void createNeighborhood(NeighborhoodDTO n) {
		dispatcher.execute(new CreateNeighborhoodAction(n), new DispatcherCallbackAsync<CreateNeighborhoodResult>() {

			@Override
			public void onSuccess(CreateNeighborhoodResult result) {
				view.displayMessage(StringConstants.SAVE_SUCCESSFULL, AlertType.SUCCESS);
			}
			
			@Override
			public void onFailure(Throwable th) {
				view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
			}
		});
	}

	@Override
	public void deleteNeighborhood(NeighborhoodDTO n) {
		DeleteNeighborhoodAction action = new DeleteNeighborhoodAction(n.getNeighborhoodId());
		dispatcher.execute(action, new DispatcherCallbackAsync<DeleteNeighborhoodResult>() {

			@Override
			public void onSuccess(DeleteNeighborhoodResult result) {
				view.displayMessage(StringConstants.NEIGHBORHOOD_DELETED, AlertType.SUCCESS);
			}
			
			@Override
			public void onFailure(Throwable th) {
				Window.alert(th.getMessage());
				view.displayMessage(StringConstants.FAILURE, AlertType.ERROR);
			}
		});
	}
	
}
