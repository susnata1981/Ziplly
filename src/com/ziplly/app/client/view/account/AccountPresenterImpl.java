package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.AbstractPresenter;
import com.ziplly.app.client.activities.AccountPresenter;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.handlers.GetAccountDetailsActionHandler;

public class AccountPresenterImpl extends AbstractPresenter implements AccountPresenter<AccountDTO> {

	private static final int TWEETS_PER_PAGE = 5;
	private int tweetPageIndex;
	
	@Inject
	public AccountPresenterImpl(
			ApplicationContext ctx,
			CachingDispatcherAsync dispatcher, 
			EventBus eventBus,
			PlaceController placeController) {
		super(ctx, dispatcher, eventBus, placeController);
  }
	
	@Override
  public void getPurchasedCoupons(int start, int pageSize) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void getCouponQRCodeUrl(Long couponTransactionId) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void printCoupon(Long couponId) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void go(AcceptsOneWidget container) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void bind() {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void goTo(Place place) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void save(AccountDTO account) {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void displayProfile() {
		if (ctx.getAccount() instanceof BusinessAccountDTO) {
			placeController.goTo(new BusinessAccountPlace());
			return;
		}

		view.displayProfile((PersonalAccountDTO) ctx.getAccount());

		// Display target neighborhood
		view.displayTargetNeighborhoods(ctx.getTargetNeighborhoodList());

		fetchTweets(ctx.getAccount().getAccountId(), tweetPageIndex, TWEETS_PER_PAGE, false);
		startInfiniteScrollThread();
		displayMap(ctx.getCurrentNeighborhood());
		getAccountDetails(new GetAccountDetailsActionHandler());
		getAccountNotifications();
		setupImageUpload();
		// Display account updates
		view.displayAccontUpdate();
  }

	@Override
  public void logout() {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void settingsLinkClicked() {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public void messagesLinkClicked() {
	  // TODO Auto-generated method stub
	  
  }

	@Override
  public SendMessagePresenter getSendMessagePresenter() {
	  // TODO Auto-generated method stub
	  return null;
  }

}
