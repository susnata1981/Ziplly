package com.ziplly.app.client.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.AccountSwitcherPlace;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class AccountSwitcherActivity extends AbstractActivity {
  private AccountSwitcherPlace place;

  public AccountSwitcherActivity(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      AccountSwitcherPlace place) {
    super(dispatcher, eventBus, placeController, ctx);
    this.place = place;
  }

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
    checkAccountLogin();
  }

  @Override
  protected void doStart() {
    if (place.getAccountId() == 0) {
      AccountDTO account = ctx.getAccount();
      gotoAccountPlace(account);
    } else {
      loadAccount(place.getAccountId());
    }
  }

  private void gotoAccountPlace(AccountDTO account) {
    if (account instanceof PersonalAccountDTO) {
      goTo(new PersonalAccountPlace(account.getAccountId()));
    } else if (account instanceof BusinessAccountDTO) {
      goTo(new BusinessAccountPlace(account.getAccountId()));
    } else {
      throw new IllegalArgumentException("Invalid account type [" + account.getAccountId() + "]");
    }
  }

  private void loadAccount(long accountId) {
    dispatcher.execute(
        new GetAccountByIdAction(accountId),
        new DispatcherCallbackAsync<GetAccountByIdResult>(eventBus) {

          @Override
          public void onSuccess(GetAccountByIdResult result) {
            gotoAccountPlace(result.getAccount());
          }

        });
  }
}
