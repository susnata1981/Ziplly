package com.ziplly.app.client.view.account;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.view.PendingActionTypes;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.shared.FieldVerifier;

public class BusinessAccountViewPresenter extends BaseAccountViewPresenter<BusinessAccountDTO> {

  public BusinessAccountViewPresenter(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      ApplicationContext ctx,
      BusinessAccountView view,
      AcceptsOneWidget panel, 
      BusinessAccountPlace place) {
    super(dispatcher, eventBus, ctx, view, panel, place);
  }

  @Override
  protected void displayAccontUpdate() {
    BusinessAccountDTO account = (BusinessAccountDTO) ctx.getAccount();
    List<PendingActionTypes> pendingActions = new ArrayList<PendingActionTypes>();

    if (account.getImages().size() == 0 
        || FieldVerifier.isEmpty(account.getWebsite())
        || FieldVerifier.isEmpty(account.getPhone())) {
      pendingActions.add(PendingActionTypes.INCOMPLETE_ACCOUNT_SETTINGS);
    }

    view.displayAccontUpdate(pendingActions);
  }

//  protected void displayPublicProfile(long accountId) {
//    dispatcher.execute(
//        new GetAccountByIdAction(accountId),
//        new GetAccountHandler<BusinessAccountDTO>(eventBus, view, this));
//    fetchTweets(accountId, tweetPageIndex, TWEETS_PER_PAGE, true);
//    startInfiniteScrollThread();
//    getPublicAccountDetails(accountId);
//  }
}
