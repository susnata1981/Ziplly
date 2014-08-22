package com.ziplly.app.client.view.common;

import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.model.ConversationDTO;

public interface MessagePresenter {
  void sendMessage(ConversationDTO conversation);

  void goTo(PersonalAccountPlace personalAccountPlace);
}
