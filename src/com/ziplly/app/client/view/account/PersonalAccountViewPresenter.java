package com.ziplly.app.client.view.account;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.view.PendingActionTypes;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.FieldVerifier;

public class PersonalAccountViewPresenter extends BaseAccountViewPresenter<PersonalAccountDTO> {

  public PersonalAccountViewPresenter(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      ApplicationContext ctx,
      IAccountView<PersonalAccountDTO> view,
      AcceptsOneWidget panel, AccountPlace place) {
    super(dispatcher, eventBus, ctx, view, panel, place);
  }

  @Override
  public void displayAccontUpdate() {
    PersonalAccountDTO account = (PersonalAccountDTO) ctx.getAccount();
    List<PendingActionTypes> pendingActions = new ArrayList<PendingActionTypes>();

    if (account.getImages().size() == 0 || account.getInterests().isEmpty()
        || FieldVerifier.isEmpty(account.getOccupation())
        || FieldVerifier.isEmpty(account.getIntroduction())) {
      pendingActions.add(PendingActionTypes.INCOMPLETE_ACCOUNT_SETTINGS);
    }

    view.displayAccontUpdate(pendingActions);
  }
  
//  protected void displayPublicProfile(long accountId) {
//    dispatcher.execute(
//        new GetAccountByIdAction(accountId),
//        new GetAccountHandler<PersonalAccountDTO>(eventBus, view, this));
//    fetchTweets(accountId, tweetPageIndex, TWEETS_PER_PAGE, true);
//    startInfiniteScrollThread();
//    getPublicAccountDetails(accountId);
//  }
}
