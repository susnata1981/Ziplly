package com.ziplly.app.client.activities;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.activities.util.ConversionUtil;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.dispatcher.DispatcherCallbackAsync;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.BusinessView;
import com.ziplly.app.client.view.BusinessView.EntityListViewPresenter;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityListAction.SearchType;
import com.ziplly.app.shared.GetEntityResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class BusinessActivity extends AbstractActivity implements
    EntityListViewPresenter,
    SendMessagePresenter {
  private BusinessView view;
  private BusinessPlace place;
  private EntityListHandler handler = new EntityListHandler(eventBus);
  private AcceptsOneWidget panel;
  private AsyncProvider<BusinessView> viewProvider;

  @Inject
  public BusinessActivity(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      PlaceController placeController,
      ApplicationContext ctx,
      BusinessPlace place,
      AsyncProvider<BusinessView> viewProvider) {
    super(dispatcher, eventBus, placeController, ctx);
    this.viewProvider = viewProvider;
    this.place = place;
  }

  @Override
  protected void setupHandlers() {
    eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
      @Override
      public void onEvent(LoginEvent event) {
        displayBusinessList();
      }
    });
  }

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    this.panel = panel;
    checkAccountLogin();
  }

  @Override
  public void doStart() {
    viewProvider.get(new DefaultViewLoaderAsyncCallback<BusinessView>() {

      @Override
      public void onSuccess(BusinessView result) {
        BusinessActivity.this.view = result;
        bind();
        view.setBackground(ctx.getCurrentNeighborhood());
        view.displayNeighborhoodFilters(getTargetNeighborhoodList());
        setupHandlers();
        displayBusinessList();
        panel.setWidget(view);
      }
    });
  }

  void displayBusinessList() {
    if (place.getAccountId() != 0) {
      view.displaySendMessageWidget(place.getAccountId());
      updateMessageWidgetWithAccountDetails(place.getAccountId());
    }

    GetEntityListAction action = getEntityListAction();
    dispatcher.execute(action, handler);
  }

  private GetEntityListAction getEntityListAction() {
    GetEntityListAction action = new GetEntityListAction(EntityType.BUSINESS_ACCOUNT);
    action.setPage(0);
    action.setPageSize(view.getPageSize());
    action.setNeedTotalEntityCount(true);
    
    if (!FieldVerifier.isEmpty(place.getPostalCode())) {
      action.setPostalCode(place.getPostalCode());
      action.setSearchType(SearchType.BY_POSTALCODE);
    } else {
      long neighborhoodId = getNeighborhoodId();
      view.setNeighborhoodId(neighborhoodId);
      action.setNeighborhoodId(neighborhoodId);
      action.setSearchType(SearchType.BY_NEIGHBORHOOD);
    }
    
    return action;
  }

  private long getNeighborhoodId() {
    return (place.getNeighborhoodId() != 0) ? place.getNeighborhoodId() 
        : ctx.getCurrentNeighborhood().getNeighborhoodId();
  }

  @Override
  public void onRangeChangeEvent(int start, int pageSize) {
    GetEntityListAction action = new GetEntityListAction(EntityType.BUSINESS_ACCOUNT);
    action.setNeedTotalEntityCount(true);
    action.setPage(start);
    action.setPageSize(pageSize);
    dispatcher.execute(action, handler);
  };

  @Override
  public void getBusinessList(GetEntityListAction action) {
    dispatcher.execute(action, handler);
  }

  private class EntityListHandler extends DispatcherCallbackAsync<GetEntityResult> {
    
    public EntityListHandler(EventBus eventBus) {
      super(eventBus);
    }
    
    @Override
    public void onSuccess(GetEntityResult result) {
      List<BusinessAccountDTO> accounts = ConversionUtil.convert(
          result.getAccounts(), BusinessAccountDTO.class);
      if (result.getCount() != null) {
        view.setTotalRowCount(result.getCount());
      }
      
      view.display(accounts);
    }
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
            view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
          }
        });
  }

  private void updateMessageWidgetWithAccountDetails(Long accountId) {
    dispatcher.execute(
        new GetAccountByIdAction(accountId),
        new DispatcherCallbackAsync<GetAccountByIdResult>() {

          @Override
          public void onSuccess(GetAccountByIdResult result) {
            view.updateMessageWidget(result.getAccount());
          }

        });
  }

  @Override
  public void bind() {
    view.setPresenter(this);
  }

  @Override
  public void onStop() {
    view.clear();
  }

  @Override
  public void go(AcceptsOneWidget container) {
  }
}
