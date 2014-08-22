package com.ziplly.app.client.view.common;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.ziplly.app.client.ApplicationContext;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.resource.StringDefinitions;
import com.ziplly.app.client.view.common.action.MessageHandler;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.shared.SendMessageAction;

public class MessagePresenterImpl extends BasePresenter implements MessagePresenter {
  private MessageHandler messageHandler;
  
  @Inject
  public MessagePresenterImpl(CachingDispatcherAsync dispatcher,
      EventBus eventBus,
      StringDefinitions stringDefinitions,
      ApplicationContext ctx) {
    super(dispatcher, eventBus, stringDefinitions, ctx);
  }

  @Override
  public void sendMessage(ConversationDTO conversation) {
    if (messageHandler == null) {
      this.messageHandler = new MessageHandler(eventBus, stringDefinitions);
    }
    
    if (conversation == null) {
      throw new IllegalArgumentException();
    }

    int size = conversation.getMessages().size();
    conversation.getMessages().get(size - 1).setSender(ctx.getAccount());
    conversation.setSender(ctx.getAccount());
    dispatcher.execute(new SendMessageAction(conversation), messageHandler);
  }

  @Override
  public void goTo(PersonalAccountPlace place) {
    super.goTo(place);
  }
}
