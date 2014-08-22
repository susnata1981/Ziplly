package com.ziplly.app.client.view.account;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetInterestAction;
import com.ziplly.app.shared.GetInterestResult;

public class PersonalAccountSettingsPresenterImpl extends AccountSettingPresenterImpl<PersonalAccountDTO, PersonalAccountSettingsPresenter> 
  implements PersonalAccountSettingsPresenter {
  
  public PersonalAccountSettingsPresenterImpl(
      CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      AcceptsOneWidget panel,
      ISettingsView<PersonalAccountDTO, PersonalAccountSettingsPresenter> view) {
    super(dispatcher, eventBus, placeController, ctx, panel, view);
    bind();
    start();
  }

  private void start() {
    setImageUploadFormSubmitCompleteHandler();
    setUploadFormActionUrl();
    view.displaySettings((PersonalAccountDTO) ctx.getAccount());
    displayAllInterests();
  }

  @Override
  public void bind() {
    view.setPresenter(this);
  }

  public void displayAllInterests() {
    dispatcher.execute(new GetInterestAction(), new DispatcherCallbackAsync<GetInterestResult>(
        eventBus) {

      @Override
      public void onSuccess(GetInterestResult result) {
        ((PersonalAccountSettingsView) view).displayAllInterests(result.getInterests());
      }

    });
  }
}
