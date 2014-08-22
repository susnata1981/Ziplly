package com.ziplly.app.client.view.account;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.shared.GetAccountByIdResult;

public class GetAccountHandler<T extends AccountDTO> extends DispatcherCallbackAsync<GetAccountByIdResult> {
  private IAccountView<T> view;
  private BaseAccountViewPresenter<T> presenter;

  public GetAccountHandler(EventBus eventBus, IAccountView<T> view, BaseAccountViewPresenter<T> presenter) {
    super(eventBus);
    this.view = view;
    this.presenter = presenter;
  }
  
  @Override
  public void onSuccess(GetAccountByIdResult result) {
    AccountDTO account = result.getAccount();
      view.displayPublicProfile((T)account);
      presenter.displayMap(result.getAccount().getLocations().get(0).getNeighborhood());
  }

  @Override
  public void onFailure(Throwable th) {
    super.onFailure(th);
    view.displayProfileSection(false);
    view.displayMessage(StringConstants.INVALID_URL, AlertType.ERROR);
  }
}