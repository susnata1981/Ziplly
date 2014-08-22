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
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.community.ResidentsView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEntityResult;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendMessageResult;

public class ResidentActivity extends AbstractActivity implements ResidentsView.EntityListViewPresenter, SendMessagePresenter {
	private ResidentsView view;
	private ResidentPlace place;
	private EntityListHandler handler = new EntityListHandler(eventBus);
	private AcceptsOneWidget panel;
	private AsyncProvider<ResidentsView> viewProvider;

	@Inject
	public ResidentActivity(CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx,
	    ResidentPlace place,
	    AsyncProvider<ResidentsView> viewProvider) {
		super(dispatcher, eventBus, placeController, ctx);
		this.viewProvider = viewProvider;
		this.place = place;
	}

	@Override
	protected void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
			@Override
			public void onEvent(LoginEvent event) {
				displayInitalRange();
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
		viewProvider.get(new DefaultViewLoaderAsyncCallback<ResidentsView>() {

			@Override
			public void onSuccess(ResidentsView result) {
				ResidentActivity.this.view = result;
				bind();
				view.setBackground(ctx.getCurrentNeighborhood());
				view.displayNeighborhoodFilters(getTargetNeighborhoodList());
				setupHandlers();
				displayInitalRange();
				panel.setWidget(view);
			}
		});
	}

	protected void displayInitalRange() {
		if (place.getAccountId() != 0) {
			view.displaySendMessageWidget(place.getAccountId());
			updateMessageWidgetWithAccountDetails(place.getAccountId());
		}

		GetEntityListAction action = getEntityListAction();
		dispatcher.execute(action, handler);
	}

	private GetEntityListAction getEntityListAction() {
    GetEntityListAction action = new GetEntityListAction(EntityType.PERSONAL_ACCOUNT);
    action.setPage(0);
    action.setPageSize(view.getPageSize());
    action.setNeedTotalEntityCount(true);
    
    long neighborhoodId = getNeighborhoodId();
    view.setNeighborhoodId(neighborhoodId);
    action.setNeighborhoodId(neighborhoodId);
    Gender gender = place.getGender() != Gender.NOT_SPECIFIED ? place.getGender() : Gender.ALL;
    action.setGender(gender);
    return action;
  }
	
  private long getNeighborhoodId() {
    return (place.getNeighborhoodId() != 0) ? place.getNeighborhoodId() : ctx
        .getCurrentNeighborhood()
        .getNeighborhoodId();
  }

  private void updateMessageWidgetWithAccountDetails(Long accountId) {
    dispatcher.execute(
        new GetAccountByIdAction(accountId),
        new DispatcherCallbackAsync<GetAccountByIdResult>(eventBus) {

          @Override
          public void onSuccess(GetAccountByIdResult result) {
            view.updateMessageWidget(result.getAccount());
          }
        });
  }

	/**
	 * Main method being called from ResidentView
	 */
	@Override
	public void getPersonalAccountList(GetEntityListAction action) {
	  action.setEntityType(EntityType.PERSONAL_ACCOUNT);
		dispatcher.execute(action, handler);
	};

	private class EntityListHandler extends DispatcherCallbackAsync<GetEntityResult> {
		
	  public EntityListHandler(EventBus eventBus) {
	    super(eventBus);
    }
	  
	  @Override
		public void onSuccess(GetEntityResult result) {
	    List<PersonalAccountDTO> accounts = ConversionUtil.convert(
	        result.getAccounts(), PersonalAccountDTO.class);
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
		    new DispatcherCallbackAsync<SendMessageResult>(eventBus) {
			    @Override
			    public void onSuccess(SendMessageResult result) {
				    view.displayMessage(StringConstants.MESSAGE_SENT, AlertType.SUCCESS);
			    }
		    });
	}

	@Override
	public void bind() {
		view.setPresenter(this);
	}

	@Override
	public void go(AcceptsOneWidget container) {
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
}
